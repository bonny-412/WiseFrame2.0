package it.bonny.app.wiseframe.manager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.List;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.bean.ImageBean;
import it.bonny.app.wiseframe.db.ManagerDB;

public class GridAdapter extends BaseAdapter {
    private final Activity activity;
    private final List<ImageBean> imageBeans;
    ManagerDB managerDB;

    public GridAdapter(Activity activity, List<ImageBean> imageBeans) {
        this.activity = activity;
        this.imageBeans = imageBeans;
        this.managerDB = new ManagerDB(this.activity);
    }

    @Override
    public int getCount() {
        return imageBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.cell_layout, parent, false);
            ImageView myImage = view.findViewById(R.id.img);
            setImage(myImage, position);
        }
        return view;
    }

    private void setImage(ImageView myImage, int position) {
        boolean existImage = true;
        if(imageBeans.get(position) != null) {
            try {
                File file = new File(imageBeans.get(position).getImagePath());
                if(!file.exists())
                    existImage = false;
            }catch (Exception e) {
                existImage = false;
                FirebaseCrashlytics.getInstance().recordException(e);
            }

            if(existImage)
                Glide.with(activity).load(imageBeans.get(position).getImagePath()).into(myImage);
            else {
                managerDB.openWriteDB();
                boolean result = managerDB.deleteById(imageBeans.get(position).getId());
                managerDB.close();
                imageBeans.remove(position);
                notifyDataSetChanged();
                if(!result)
                    FirebaseCrashlytics.getInstance().log("Delete image by id error");
            }
        }
    }
}
