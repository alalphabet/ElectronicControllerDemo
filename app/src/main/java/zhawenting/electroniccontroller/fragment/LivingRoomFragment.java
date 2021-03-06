package zhawenting.electroniccontroller.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import zhawenting.electroniccontroller.R;
import zhawenting.electroniccontroller.adapter.FixtureAdapter;
import zhawenting.electroniccontroller.base.BaseFragment;
import zhawenting.electroniccontroller.entity.FixtureEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LivingRoomFragment extends BaseFragment implements FixtureAdapter.ICallback {

    FixtureAdapter listAdapter;
    List<FixtureEntity> listItem;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    Context context;

    private CameraManager manager;
    private Camera camera = null;

    public LivingRoomFragment() {
        // Required empty public constructor
    }

    @Override
    protected void iniData() {
        context = getActivity();
        listItem = new ArrayList();
        listItem.add(new FixtureEntity("Temperature", "Loading..."));
        listItem.add(new FixtureEntity("Light", "Off"));
        listItem.add(new FixtureEntity("TV", "Off"));

        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.getCameraIdList();
        } catch (CameraAccessException e) {
            Log.e("error", e.getMessage());
        }
    }

    @Override
    protected void iniView() {

        listAdapter = new FixtureAdapter(context, listItem);
        listAdapter.setListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(listAdapter);

    }

    public void setTemperature(int temperature){
        (listItem.get(0)).setFixtureState(temperature + " ℃");
        listAdapter.notifyItemChanged(0);
    }

    @Override
    public int getLayoutRes() {
        return (R.layout.fragment_livingroom);
    }


    private void lightSwitch(final boolean lightStatus) {
        if (lightStatus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PackageManager pm = context.getPackageManager();
                final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                for (final FeatureInfo f : features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        if (null == camera) {
                            camera = Camera.open();
                        }
                        final Camera.Parameters parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                    }
                }
            }
        }
    }

    @Override
    public void fixutureAdapterCallback(int type, int position) {
        switch (position) {
            case 1:
                if (type == 0)
                    lightSwitch(true);
                else
                    lightSwitch(false);
                break;
        }
    }
}

