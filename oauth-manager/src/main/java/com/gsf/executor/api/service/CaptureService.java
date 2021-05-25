package com.gsf.executor.api.service;

import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.entity.Captured;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
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

    private List<String> validation = Arrays.asList("GET / HTTP/1.1",
            "GET /client/authorize",
            "HTTP/1.1 302",
            "GET /authorize?",
            "GET /authorizationServer/authorize/",
            "POST /oauth2/authorize", //keyrock
            "HTTP/1.1 307",
            "GET /client/callback",
            "GET /csrf HTTP/1.1",
            "POST /oauth2/token HTTP/1.1", //keyrock
            "POST /authorizationServer/oauth2/token HTTP/1.1",
            "POST /authorizationServer/mixup/oauth2/token HTTP/1.1");

    private List<String> validationVSRFFlow = Arrays.asList("GET / HTTP/1.1",
            "GET /client/authorize",
            "HTTP/1.1 302",
            "GET /authorize?",
            "GET /authorizationServer/authorize/",
            "HTTP/1.1 307",
            "GET /client/callback",
            "POST /authorizationServer/oauth2/token HTTP/1.1");

    //

    public static void main(String[] args) {
        UserTemplate userTemplate = UserTemplateMemoryRepository.findById(1);
        new CaptureService().execute(userTemplate, "async");
    }

    public CaptureTemplate execute(UserTemplate user, String type) {

        CaptureTemplate capture = new CaptureTemplate();

        List<CaptureTemplate> capturedList;

        if ("sync".equalsIgnoreCase(type)) {
            capturedList = CaptureTemplateMemoryRepository.getAllCaptureTemplate();
        } else {
            capturedList = CaptureTemplateMemoryRepository.getAllCaptureTemplateASYNC();
        }

        capture.setId(capturedList.size() + 1);
        capture.setType(type);
        capture.setInitFile(capturedList.size() > 0 ? capturedList.get(capturedList.size() - 1).getEndFile() : 0);
        capture.setCaptureDate(LocalDateTime.now());
        capture.setUser(user);

        CaptureTemplateMemoryRepository.allPcapFiles.add(type+"_init_id_" + capture.getId());


        int attempt = 1;
        Pcap pcap = null;
        do {
            try {
               // Thread.sleep(5000L);
                //pcap = Pcap.openStream("C:\\dev\\docker\\oauth-fiware\\fiware-idm\\tcpdump\\tcpdump.pcap");
                pcap = Pcap.openStream("C:\\dev\\docker\\oauth-fiware\\oauth-manager\\src\\main\\resources\\tcpdump22.pcap");

                execute(capture, pcap, type);

                pcap.close();
                pcap = null;

                //get Capture for specif ID and remove duplicates
                List<String> listBySpecificID = getCapturedByIdAndType(capture.getId(), type);
                listBySpecificID = removeDuplicates(listBySpecificID);

                //add at CaptureTemplate capture
                add(capture, listBySpecificID);

                //verify vulnerabilities
                if ("async".equalsIgnoreCase(type)) {
                    verifyVulnerabilities(capture);
                }

                System.out.println("Attempt OK >> " + attempt);
                attempt = 0;

            } catch (Exception e) {
                System.out.println("Attempt >> " + attempt++);
                pcap.close();
                pcap = null;
                e.printStackTrace();
            }
        } while (attempt > 0);

        System.out.println(capture);

        return capture;

    }

    private void verifyVulnerabilities(CaptureTemplate capture) {
        capture.getCaptureList()
                .forEach(captured -> {

                    if (captured.getTitle().equalsIgnoreCase("HTTP/1.1 307")) {
                        captured.getVulnerabilities().add("307 Redirect Vulnerability");

                    } else if (captured.getTitle().equalsIgnoreCase("GET /authorize?")
                        || captured.getTitle().equalsIgnoreCase("GET /authorizationServer/authorize/")
                        || captured.getTitle().equalsIgnoreCase("GET /client/callback")) {

                        //avaliar se contem parametro state preenchido e se contem parametro iss

                        String url = captured.getValue().split(" ")[1];

                        String getState = verifyUrl(url, "state");
                        if(getState == null || "".equalsIgnoreCase(getState)) {
                            captured.getVulnerabilities().add("CSRF Vulnerability");
                        }

                        String getIss = verifyUrl(url, "iss");
                        if(getIss == null || "".equalsIgnoreCase(getIss)) {
                            captured.getVulnerabilities().add("MixUp Vulnerability");
                        }
                    }

                });

    }

    protected String verifyUrl(String url, String value) {

        String[] values = url.split("\\?")[1].split("&");
        for (String v: values) {
            if (v.startsWith(value)) {
                if (v.split("=").length > 1) {
                    return v.split("=")[1];
                }

            }
        }

        return "";
    }

    private List<String> removeDuplicates(List<String> listBySpecificID) {

        List<String> result = new ArrayList<>();
        String duplicateLine = "";

        for (String line : listBySpecificID) {

            if (!duplicateLine.equalsIgnoreCase(line)) {
                result.add(line);
            }
            duplicateLine = line;
        }

        return result;
    }

    private List<String> getCapturedByIdAndType(int id, String type) {

        List<String> result = new ArrayList<>();
        AtomicBoolean end = new AtomicBoolean(false);

        CaptureTemplateMemoryRepository.allPcapFiles
                .forEach(line -> {

                    if (line.equalsIgnoreCase(type + "_init_id_" + id) || end.get()) {

                        result.add(line);
                        end.set(true);

                        if (line.equalsIgnoreCase(type + "_end_id_" + id)) {
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

    private void execute(CaptureTemplate capture, Pcap pcap, String type) throws IOException {

        pcap.loop(new PacketHandler() {
            int counter = 1;

            long init = CaptureTemplateMemoryRepository.allPcapFiles.size();

            @Override
            public boolean nextPacket(Packet packet) throws IOException {
                if (packet.hasProtocol(Protocol.TCP)) {

                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                    Buffer buffer = tcpPacket.getPayload();
                    if (buffer != null) {
                        //System.out.println("TCP (" + counter + "): " + buffer);

                        if (counter >= init) {
                            CaptureTemplateMemoryRepository.allPcapFiles.add(buffer.toString());
                        }
                        counter++;
                    }


                }
                return true;
            }


        });

        CaptureTemplateMemoryRepository.allPcapFiles.add(type + "_end_id_" + capture.getId());

        CaptureTemplateMemoryRepository.allPcapFiles
                .forEach(s -> {
                    //System.out.println(s);
                    if (s.contains("_end_id_")) {
                        System.out.println(s);
                    }
                });


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


}
