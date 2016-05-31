package org.tribler.app_to_appcommunicator.PEX;

import com.hypirion.bencode.BencodeReadException;
import com.hypirion.bencode.BencodeReader;
import com.hypirion.bencode.BencodeWriter;

import org.tribler.app_to_appcommunicator.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by jaap on 5/2/16.
 */
public class PexMessage extends HashSet<Peer> {

    private InetSocketAddress destinationAddress;

    public static PexMessage createFromStream(InputStream stream) throws IOException, BencodeReadException, PexException {
        BencodeReader reader = new BencodeReader(stream);
        Map<String, Object> dict = reader.readDict();

        List peers = (List) dict.get("added");

        PexMessage pexMessage = new PexMessage();
        for (Object peerMap : peers) {
            Map map = (Map) peerMap;
            Inet4Address address = (Inet4Address) Inet4Address.getByName((String) map.get("address"));
            long port = (long) map.get("port");
            pexMessage.add(new Peer(new InetSocketAddress(address, (int) port), null));
        }
        if (dict.containsKey("destination")) {
            Map destinationAddress = (Map) dict.get("destination");
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName((String) destinationAddress.get("address")), (int) (long) destinationAddress.get("port"));
            System.out.println("received address: " + address);
            pexMessage.setDestinationAddress(address);
        }
        return pexMessage;
    }

    public static PexMessage createFromByteBuffer(ByteBuffer buffer) throws BencodeReadException, PexException, IOException {
        return createFromStream(new ByteBufferinputStream(buffer));
    }

    public InetSocketAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(InetSocketAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void writeToStream(OutputStream out) throws IOException {
        Map<String, Object> utPex = new HashMap<>();
        List<Map<String, Object>> peers = new ArrayList<>();
        for (Peer peer : this) {
            Map map = new HashMap();
            map.put("address", peer.getExternalAddress().getHostAddress());
            map.put("port", peer.getPort());
            peers.add(map);
        }

        utPex.put("added", peers);
        if (destinationAddress != null) {
            Map dMap = new HashMap();
            dMap.put("port", destinationAddress.getPort());
            dMap.put("address", destinationAddress.getAddress().getHostAddress());
            utPex.put("destination", dMap);
        }

        BencodeWriter writer = new BencodeWriter(out);
        writer.write(utPex);
    }

    public void writeToByteBuffer(ByteBuffer buffer) throws IOException {
        writeToStream(new ByteBufferOutputStream(buffer));
    }
}
