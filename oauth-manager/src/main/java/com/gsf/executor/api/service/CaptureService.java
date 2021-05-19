package com.gsf.executor.api.service;

import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.entity.Captured;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;
import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CaptureService {

    private  List<String> validation = Arrays.asList("GET / HTTP/1.1",
            "GET /client/authorize",
            "HTTP/1.1 302",
            "GET /authorize?",
            "GET /authorizationServer/authorize/",
            "POST /oauth2/authorize", //keyrock
            "HTTP/1.1 307",
            "GET /client/callback",
            "POST /oauth2/token HTTP/1.1", //keyrock
            "POST /authorizationServer/oauth2/token HTTP/1.1");

    private  List<String> validationVSRFFlow = Arrays.asList("GET / HTTP/1.1",
            "GET /client/authorize",
            "HTTP/1.1 302",
            "GET /authorize?",
            "GET /authorizationServer/authorize/",
            "HTTP/1.1 307",
            "GET /client/callback",
            "POST /authorizationServer/oauth2/token HTTP/1.1");


    public CaptureTemplate execute(UserTemplate user, String flowType) {

        CaptureTemplate capture = new CaptureTemplate();

        List<CaptureTemplate> capturedList = CaptureTemplateMemoryRepository.getAllCaptureTemplate();

        capture.setId(capturedList.size() + 1);
        capture.setType(flowType);
        capture.setInitFile(capturedList.size() > 0 ? capturedList.get(capturedList.size() - 1).getEndFile() : 0);
        capture.setCaptureDate(LocalDateTime.now());
        capture.setUser(user);

        CaptureTemplateMemoryRepository.allPcapFiles.add("init id:"+capture.getId());

        try {

            Pcap pcap = Pcap.openStream("C:\\dev\\docker\\oauth-fiware\\fiware-idm\\tcpdump\\tcpdump.pcap");

            execute(capture, pcap);

            pcap.close();
            pcap = null;

            //get Capture for specif ID and remove duplicates
            List<String> listBySpecificID = getCapturedById(capture.getId());
            listBySpecificID = removeDuplicates(listBySpecificID);

            //add at CaptureTemplate capture
            add(capture, listBySpecificID);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return capture;

    }

    private List<String> removeDuplicates(List<String> listBySpecificID) {

        List<String> result = new ArrayList<>();
        String duplicateLine = "";

        for (String line: listBySpecificID) {

            if (!duplicateLine.equalsIgnoreCase(line)) {
                result.add(line);
            }
            duplicateLine = line;
        }

        return result;
    }

    private List<String> getCapturedById(int id) {

        List<String> result = new ArrayList<>();
        AtomicBoolean end = new AtomicBoolean(false);

        CaptureTemplateMemoryRepository.allPcapFiles
                .forEach(line -> {

                    if (line.equalsIgnoreCase("init id:" + id) || end.get()) {

                        result.add(line);
                        end.set(true);

                        if (line.equalsIgnoreCase("end id:" + id)) {
                            end.set(false);
                        }
                    }
                });

        return result;

    }

    private void add(CaptureTemplate capture, List<String> listBySpecificID) {
        AtomicBoolean findForPostResponse = new AtomicBoolean(false);
        AtomicInteger counter = new AtomicInteger(1);

        listBySpecificID.forEach(line -> {

                    if (findForPostResponse.get()) {
                        if (line.contains("HTTP/1.1 200")) {

                            Captured captured = new Captured();
                            captured.setId(counter.get());
                            captured.setTitle("HTTP/1.1 200");
                            captured.setValue(line);

                            capture.getCaptureList().add(captured);

                            capture.setEndFile(counter.addAndGet(1));

                            findForPostResponse.set(false);
                        }
                    }

                    validation.forEach(value -> {
                        if (line.contains(value)) {
                            Captured captured = new Captured();
                            captured.setId(counter.get());
                            captured.setTitle(value);
                            captured.setValue(line);

                            capture.getCaptureList().add(captured);

                            findForPostResponse.set(true);
                        }
                    });

                    capture.setEndFile(counter.addAndGet(1));

                });

    }

    private void execute(CaptureTemplate capture, Pcap pcap) {
        try {

            pcap.loop(new PacketHandler() {
                int counter = 1;

                long init =  CaptureTemplateMemoryRepository.allPcapFiles.size();

                @Override
                public boolean nextPacket(Packet packet) throws IOException {
                    if (packet.hasProtocol(Protocol.TCP)) {

                        TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                        Buffer buffer = tcpPacket.getPayload();
                        if (buffer != null) {
                            System.out.println("TCP (" + counter + "): " + buffer);

                            if(counter >= init) {
                                CaptureTemplateMemoryRepository.allPcapFiles.add(buffer.toString());
                            }
                            counter++;
                        }


                    }
                    return true;
                }


            });

            CaptureTemplateMemoryRepository.allPcapFiles.add("end id:"+capture.getId());

            CaptureTemplateMemoryRepository.allPcapFiles
                    .forEach(s -> {
                        //System.out.println(s);
                        if(s.contains("end id:")) {
                            System.out.println(s);
                        }
                    });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareFile() {
        String FILE_TO_MOVE = "C:\\dev\\docker\\oauth-fiware\\fiware-idm\\tcpdump\\tcpdump-header.pcap";
        String TARGET_FILE = "C:\\dev\\docker\\oauth-fiware\\fiware-idm\\tcpdump\\tcpdump.pcap";

        //delete original tcpdump
        File fileTcpdump = new File(TARGET_FILE);
        fileTcpdump.delete();

        //create new tcpdump apartir do header

        File fileTcpdumpHeader = new File(FILE_TO_MOVE);
        File newfileTcpdump = new File(TARGET_FILE);

        Path oldPath = Paths.get(FILE_TO_MOVE);
        Path newPath = newfileTcpdump.toPath();
        try {
            Files.copy(oldPath, newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fileToMove.delete();
        //Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

    }

    public static void main(String[] args) {

        String cpf = "000221.982.048-35";
        String novoCpf = cpf.substring(3);
        System.out.println(novoCpf);
    }

    }
