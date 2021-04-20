package com.gsf.executor.api.service;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CaptureService {

    public Map capture() {

        Map result = new TreeMap<String, String>();
        try {

        	final Pcap pcap = Pcap.openStream("src/main/resources/tcpdump14.pcap");

            pcap.loop(new PacketHandler() {
                int counter = 1;
                boolean findForPostResponse = false;

                @Override
                public boolean nextPacket(Packet packet) throws IOException {
                    if (packet.hasProtocol(Protocol.TCP)) {

                        TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                        Buffer buffer = tcpPacket.getPayload();
                        if (buffer != null) {

                            if (findForPostResponse) {
                                if (buffer.toString().contains("HTTP/1.1 200")) {
                                    System.out.println("TCP (" + counter + "): " + buffer);
                                    counter++;
                                    findForPostResponse = false;
                                    result.put("" + counter, buffer);
                                    return true;
                                }
                            }

                            if (buffer.toString().contains("GET / HTTP/1.1") || buffer.toString().contains("GET /client/authorize")
                                    || buffer.toString().contains("HTTP/1.1 302") || buffer.toString().contains("GET /authorize?")
                                    || buffer.toString().contains("GET /authorizationServer/authorize/")
                                    || buffer.toString().contains("HTTP/1.1 307")
                                    || buffer.toString().contains("GET /client/callback") || buffer.toString().contains("POST /authorizationServer/oauth2/token HTTP/1.1")
                                    || buffer.toString().contains("xHTTP/1.1 200")) {


                                System.out.println("TCP (" + counter + "): " + buffer);
                                result.put("" + counter, buffer);
                                counter++;

                                if (buffer.toString().contains("POST /authorizationServer/oauth2/token HTTP/1.1")) {
                                    findForPostResponse = true;
                                }

                            }

                        }
                    }
                    return true;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;


    }
}
