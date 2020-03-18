package com.example.locationusingretrofitfrombonnysir;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationusingretrofitfrombonnysir.databinding.FragmentMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private Context context;
    private final int LOCATION_REQUEST_CODE=111;
    FragmentMainBinding binding;
    private FusedLocationProviderClient providerClient;
    private Geocoder geocoder;



    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentMainBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        providerClient= LocationServices.getFusedLocationProviderClient(context);

        if (CheckLocationPermission()){
                getDeviceCurrentLoacation();
        }


    }

    public boolean CheckLocationPermission(){
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION};
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(permissions,LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if (requestCode== LOCATION_REQUEST_CODE &&
       grantResults[0]==PackageManager.PERMISSION_GRANTED){
           getDeviceCurrentLoacation();
           
       }

    }

    private void getDeviceCurrentLoacation() {
        providerClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                      /*  Toast.makeText(context, "", Toast.LENGTH_SHORT).show();*/
                        if (location==null){
                            return;
                        }
                        double latitude=location.getLatitude();
                        double longitude=location.getLongitude();

                        binding.latTV.setText(String.valueOf(latitude));
                        binding.lngTV.setText(String.valueOf(longitude));

                        getAddressFromLatLng(latitude,longitude);



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAddressFromLatLng(double latitude, double longitude) {
        geocoder=new Geocoder(context);
        try {
            List<Address> addressList= geocoder.getFromLocation(latitude,longitude,1);
            String addressLine=addressList.get(0).getAddressLine(0);
            binding.addressTV.setText(addressLine);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
