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
import com.example.atm.databinding.ItemTransactionBinding;
import com.example.atm.ui.activities.MainActivity;
import com.example.atm.ui.activities.ProfileActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final Context context;
    private final List<Transaction> transactions;
    private final LayoutInflater layoutInflater;
    private TransactionAdapterCallback callback;
    private ExecutorService executorService;
    public TransactionAdapter(Context context) {
        this.context = context;
        this.transactions = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public TransactionAdapter(Context context, TransactionAdapterCallback callback) {
        this(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void remove(Transaction transaction) {
        int index = transactions.indexOf(transaction);
        if (index != -1) {
            transactions.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, transactions.size());
        }
    }

    public void update(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTransactionBinding binding;

        public ViewHolder(@NonNull ItemTransactionBinding binding) {
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
            binding.expireDateTv.setText(formattedDate);
            binding.amountTv.setText(String.valueOf(transaction.getAmount()));
            binding.transactionTypeTv.setText(String.valueOf(transaction.getTransactionType()));
            binding.transactionTypeTv.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, transaction.getTypeColor())
            );


            executorService.execute(new GetUsernameByIdRunnable(context.getApplicationContext(), transaction.getUserId(), new ResultListener<User>() {
                @Override
                public void onSuccess(User user) {
                    ((ProfileActivity) context).runOnUiThread(() -> {
                        binding.usernameTv.setText(user.getUsername());
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    ((ProfileActivity) context).runOnUiThread(() -> {
                        binding.usernameTv.setText("Unknown User");
                    });
                }


            }));

            binding.mainLay.setOnLongClickListener(v -> {
                if (callback != null) {
                    callback.onItemLongClicked(transaction);
                }
                return true;
            });
        }
    }

    public interface TransactionAdapterCallback {

        void onItemLongClicked(Transaction transaction);
    }
}

