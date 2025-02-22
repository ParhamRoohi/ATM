package com.example.atm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atm.ResultListener;
import com.example.atm.data.db.runnables.user.GetUsernameByIdRunnable;
import com.example.atm.data.models.Transaction;
import com.example.atm.data.models.User;
import com.example.atm.databinding.ItemRecentBinding;
import com.example.atm.ui.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private final Context context;
    private final List<Transaction> transactions;
    private final LayoutInflater layoutInflater;
    private RecentAdapterCallback callback;

    private ExecutorService executorService;

    public RecentAdapter(Context context, RecentAdapterCallback callback) {
        this.context = context;
        this.transactions = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentBinding binding = ItemRecentBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        if (transactions.size() <= 8) {
            return transactions.size();
        } else {
            return 8;
        }
    }

    public void itemInsertedOnTop(Transaction transaction) {
        transactions.add(0, transaction);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, transactions.size() - 1);
    }

    public void update(List<Transaction> transaction) {
        this.transactions.clear();
        this.transactions.addAll(transaction);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRecentBinding binding;

        public ViewHolder(@NonNull ItemRecentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(int position) {
            Transaction transaction = transactions.get(position);

            Date transactionDate = transaction.getTransactionDate();
            String formattedDate = "";
            if (transactionDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formattedDate = sdf.format(transactionDate);
            }
            binding.transactionDateTv.setText(formattedDate);
            binding.amountTv.setText(String.valueOf(transaction.getAmount()));
            binding.transactionTypeTv.setText(transaction.getTransactionType());

            binding.transactionTypeTv.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, transaction.getTypeColor())
            );

            executorService.execute(new GetUsernameByIdRunnable(context.getApplicationContext(), transaction.getUserId(), new ResultListener<User>() {
                @Override
                public void onSuccess(User user) {
                    ((MainActivity) context).runOnUiThread(() -> {
                        binding.usernameTv.setText(user.getUsername());
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    ((MainActivity) context).runOnUiThread(() -> {
                        binding.usernameTv.setText("Unknown User");
                    });
                }


            }));
            binding.recentLay.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onItemClicked(transaction);
                }
            });

        }
    }

    public interface RecentAdapterCallback {
        void onItemClicked(Transaction transaction);
    }
}