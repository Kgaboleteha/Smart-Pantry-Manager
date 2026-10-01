package za.ac.richfield.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnPantryItemListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());

        if (item.getExpiryDate() != null && !item.getExpiryDate().trim().isEmpty()) {
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnEdit, btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_item_expiry);
            btnEdit = itemView.findViewById(R.id.btn_edit_item);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}