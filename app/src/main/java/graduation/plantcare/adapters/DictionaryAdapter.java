package graduation.plantcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import graduation.plantcare.R;
import graduation.plantcare.fragments.DictionaryItem;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryAdapterViewHolder> {
    private final List<DictionaryItem> dictionaryItems;
    private final Context context;
    private int lastPositionAnimated = -1;

    public DictionaryAdapter(Context context, List<DictionaryItem> dictionaryItems) {
        this.context = context;
        this.dictionaryItems = dictionaryItems;
    }

    @NonNull
    @Override
    public DictionaryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_item, parent, false);
        return new DictionaryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DictionaryItem item = dictionaryItems.get(position);
        holder.dictionaryItemName.setText(item.getName());
        holder.dictionaryItemDescription.setText(item.getDescription());
        holder.icon.setImageResource(item.getIconResId());

        if (position > lastPositionAnimated) {
            Animation anim = createSlideUpAnimation();
            anim.setStartOffset(position * 100L);
            holder.itemView.startAnimation(anim);
            lastPositionAnimated = position;
        } else {
            holder.itemView.clearAnimation();
        }
    }

    private Animation createSlideUpAnimation() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        anim.setDuration(500);
        return anim;
    }

    @Override
    public int getItemCount() {
        return dictionaryItems.size();
    }

    static class DictionaryAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView dictionaryItemName, dictionaryItemDescription;
        ImageView icon;
        DictionaryAdapterViewHolder(View itemView) {
            super(itemView);
            dictionaryItemName = itemView.findViewById(R.id.dictionaryItemName);
            dictionaryItemDescription = itemView.findViewById(R.id.dictionaryItemDescription);
            icon = itemView.findViewById(R.id.dictionaryIcon);
        }
    }
}
