package com.example.hatchatmobile1.ServerAPI;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.hatchatmobile1.Entities.AllChatResponse;
import com.example.hatchatmobile1.Entities.ContactChatResponse;
import com.example.hatchatmobile1.Entities.MessageResponse;
import com.example.hatchatmobile1.Entities.NewContactChatRequest;
import com.example.hatchatmobile1.Entities.User;
import com.example.hatchatmobile1.ViewModals.SettingsViewModal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactsAPI {

    private Retrofit retrofit;

    private SettingsViewModal settingsViewModal;

    private ContactsWebServiceAPI contactsWebServiceAPI;

    private String baseUrl;

    private Gson gson;

    private String token;

    public ContactsAPI(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
        gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        contactsWebServiceAPI = retrofit.create(ContactsWebServiceAPI.class);
    }

    public void postNewContactChat(String username, final OnContactChatResponseListener listener) {
        new Thread(() -> {
            try {
                Response<ContactChatResponse> response = contactsWebServiceAPI.AddContactChat(new NewContactChatRequest(username), token).execute();

                if (response.isSuccessful()) {
                    ContactChatResponse contactChatResponse = response.body();
                    listener.onResponse(contactChatResponse); // Pass the response to the listener
                } else {
                    listener.onError("Request failed with code: " + response.code());
                }
            } catch (IOException e) {
                listener.onError(e.getMessage());
            }
        }).start();
    }


    public void getMessagesForContact(int contactId, final OnGetMessagesResponseListener listener) {
        Call<List<MessageResponse>> call = contactsWebServiceAPI.GetMessagesForContact(token, contactId);
        call.enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                if (response.isSuccessful()) {
                    List<MessageResponse> messages = response.body();
                    if (messages != null) {
                        listener.onResponse(messages); // Pass the messages to the listener
                    } else {
                        listener.onError("Response body is empty");
                    }
                } else {
                    listener.onError("Request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }


    public void getAllChats(final OnGetAllChatsResponseListener listener) {
        Call<List<AllChatResponse>> call = contactsWebServiceAPI.GetAllChats(token);
        call.enqueue(new Callback<List<AllChatResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AllChatResponse>> call, @NonNull Response<List<AllChatResponse>> response) {
                if (response.isSuccessful()) {
                    List<AllChatResponse> chats = response.body();
                    if (chats != null) {
                        listener.onResponse(chats); // Pass the chats to the listener
                    } else {
                        listener.onError("Response body is empty");
                    }
                } else {
                    listener.onError("Request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllChatResponse>> call, @NonNull Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnGetAllChatsResponseListener {
        void onResponse(List<AllChatResponse> chats);

        void onError(String error);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        contactsWebServiceAPI = retrofit.create(ContactsWebServiceAPI.class);
    }

    public interface OnContactChatResponseListener {
        void onResponse(ContactChatResponse contactChatResponse);

        void onError(String error);
    }

    public interface OnGetMessagesResponseListener {
        void onResponse(List<MessageResponse> messages);

        void onError(String error);
    }
}
