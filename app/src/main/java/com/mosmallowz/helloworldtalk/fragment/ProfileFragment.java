package com.mosmallowz.helloworldtalk.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.mosmallowz.helloworldtalk.R;
import com.mosmallowz.helloworldtalk.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by mossi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class ProfileFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;

    LinearLayout changeName;
    LinearLayout changePassword;
    AppCompatTextView nameProfile;
    AppCompatTextView emailProfile;
    ImageView changeImgeProfile;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainsRef;
    ProgressDialog progress;

    public static final int REQUEST_GALLERY = 1;
    private static final int PICK_FROM_GALLERY = 1;


    public ProfileFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mountainsRef = storageRef.child("imagesProfile/" + user.getUid() + "/photoUrl");
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        changeName = rootView.findViewById(R.id.change_name);
        changePassword = rootView.findViewById(R.id.change_password);
        nameProfile = rootView.findViewById(R.id.name_profile);
        emailProfile = rootView.findViewById(R.id.email_profile);
        changeImgeProfile = rootView.findViewById(R.id.change_img_profile);

        if (user != null) {
            nameProfile.setText(user.getDisplayName());
            emailProfile.setText(user.getEmail());

            try {
                Glide.with(getActivity())
                        .load(user.getPhotoUrl().toString())
                        .into(changeImgeProfile);
            } catch (NullPointerException ex) {
            }

        }

        changeImgeProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        changeImgeProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent
//                        , "Select Picture"), REQUEST_GALLERY);
//            }
//        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chaneName();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);

                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode
            , Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri uri = data.getData();
            try {
                progress = ProgressDialog.show(getActivity(), "Please wait",
                        "Updating image", true);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                changeImgeProfile.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataA = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(dataA);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("uploading", "fail" + exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String imageUrl = uri.toString();
                                        //createNewPost(imageUrl);
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(Uri.parse(imageUrl))
                                                .build();

                                        user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Users users = new Users();
                                                        users.setName(user.getDisplayName());
                                                        users.setEmail(user.getEmail());
                                                        users.setIdRoom("");
                                                        users.setPhotoUrl(imageUrl);
                                                        users.setUid(user.getUid());
                                                        myRef.child(user.getUid()).setValue(users);
                                                        progress.dismiss();
                                                        Toast.makeText(getActivity(), "change profile picture successfully.", Toast.LENGTH_SHORT).show();
                                                        Log.d("uploadimg", "success");
//
                                                    }
                                                }
                                            });
                                    }
                                });
                            }
                        }


                    }
                });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePassword() {
        final EditText input = new EditText(getActivity());
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder builderChangePassword = new AlertDialog.Builder(getActivity());
        builderChangePassword.setTitle("Type your new password");
        builderChangePassword.setView(input);
        builderChangePassword.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (!password.isEmpty()) {
                    user.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "change password successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "please input password.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builderChangePassword.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderChangePassword.show();
    }

    private void chaneName() {
        final EditText input = new EditText(getActivity());
        input.setText(user.getDisplayName());
        input.setSelection(user.getDisplayName().length());
        AlertDialog.Builder builderChangeName = new AlertDialog.Builder(getActivity());
        builderChangeName.setTitle("Type your name");
        builderChangeName.setView(input);
        builderChangeName.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = input.getText().toString();

                if (!name.isEmpty()) {
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Users users = new Users();
                                            users.setName(name);
                                            users.setEmail(user.getEmail());
                                            users.setUid(user.getUid());
                                            users.setIdRoom("null");
                                            try {
                                                users.setPhotoUrl(user.getPhotoUrl().toString());
                                            } catch (NullPointerException ex) {
                                                users.setPhotoUrl("");
                                            }

                                            myRef.child(user.getUid()).setValue(users);
                                            nameProfile.setText(name);
                                            Toast.makeText(getActivity(), "change name successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                } else {
                    Toast.makeText(getActivity(), "please input your name.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builderChangeName.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderChangeName.show();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
