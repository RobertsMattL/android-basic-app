package com.example.basicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class ContactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.contactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new ContactsAdapter(getSampleContacts()));
    }

    private List<Contact> getSampleContacts() {
        return Arrays.asList(
                new Contact("Alice Johnson", "(555) 123-4567", "alice@example.com"),
                new Contact("Bob Smith", "(555) 234-5678", "bob@example.com"),
                new Contact("Charlie Brown", "(555) 345-6789", "charlie@example.com"),
                new Contact("Diana Prince", "(555) 456-7890", "diana@example.com"),
                new Contact("Edward Norton", "(555) 567-8901", "edward@example.com"),
                new Contact("Fiona Green", "(555) 678-9012", "fiona@example.com"),
                new Contact("George Lucas", "(555) 789-0123", "george@example.com"),
                new Contact("Hannah Lee", "(555) 890-1234", "hannah@example.com"),
                new Contact("Isaac Newton", "(555) 901-2345", "isaac@example.com"),
                new Contact("Julia Roberts", "(555) 012-3456", "julia@example.com")
        );
    }

    static class Contact {
        final String name;
        final String phone;
        final String email;

        Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }
    }

    static class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private final List<Contact> contacts;

        ContactsAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.name.setText(contact.name);
            holder.phone.setText(contact.phone);
            holder.email.setText(contact.email);
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView name;
            final TextView phone;
            final TextView email;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.contactName);
                phone = itemView.findViewById(R.id.contactPhone);
                email = itemView.findViewById(R.id.contactEmail);
            }
        }
    }
}
