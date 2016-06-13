package org.tribler.app_to_appcommunicator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jaap on 5/4/16.
 */
public class PeerListAdapter extends ArrayAdapter<Peer> {
    private final Context context;
    private boolean incoming;

    public PeerListAdapter(Context context, int resource, List<Peer> peerConnectionList, boolean incoming) {
        super(context, resource, peerConnectionList);
        this.context = context;
        this.incoming = incoming;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.peer_connection_list_item, parent, false);

            holder = new ViewHolder();
            holder.mStatusIndicator = (TextView) convertView.findViewById(R.id.status_indicator);
            holder.mCarrier = (TextView) convertView.findViewById(R.id.carrier);
            holder.mPeerId = (TextView) convertView.findViewById(R.id.peer_id);
            holder.mDestinationAddress = (TextView) convertView.findViewById(R.id.destination_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Peer peer = getItem(position);

        holder.mPeerId.setText(peer.getPeerId() == null ? "" : peer.getPeerId().substring(0, 4));
        if (peer.hasReceivedData()) {
            if (peer.isAlive()) {
                holder.mStatusIndicator.setTextColor(context.getResources().getColor(R.color.colorStatusConnected));
            } else {
                holder.mStatusIndicator.setTextColor(context.getResources().getColor(R.color.colorStatusCantConnect));
            }
            holder.mDestinationAddress.setText(String.format("%s:%d", peer.getExternalAddress(), peer.getPort()));
        } else {
            if (peer.isAlive()) {
                holder.mStatusIndicator.setTextColor(context.getResources().getColor(R.color.colorStatusConnecting));
            } else {
                holder.mStatusIndicator.setTextColor(context.getResources().getColor(R.color.colorStatusCantConnect));
            }
            holder.mDestinationAddress.setText(String.format("%s:%d", peer.getExternalAddress(), peer.getPort()));
        }

        return convertView;
    }

    private String connectionTypeString(int connectionType) {
        switch (connectionType) {
            case ConnectivityManager.TYPE_WIFI:
                return "Wifi";
            case ConnectivityManager.TYPE_BLUETOOTH:
                return "Bluetooth";
            case ConnectivityManager.TYPE_ETHERNET:
                return "Ethernet";
            case ConnectivityManager.TYPE_MOBILE:
                return "Mobile";
            case ConnectivityManager.TYPE_MOBILE_DUN:
                return "Mobile dun";
            case ConnectivityManager.TYPE_VPN:
                return "VPN";
            default:
                return "Unknwon";
        }
    }

    static class ViewHolder {
        TextView mPeerId;
        TextView mCarrier;
        TextView mDestinationAddress;
        TextView mStatusIndicator;
    }

}
