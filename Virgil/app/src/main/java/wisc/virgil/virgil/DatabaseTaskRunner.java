package wisc.virgil.virgil;

import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import android.graphics.Bitmap;

/**
 * Created by TylerPhelps on 4/1/16.
 */
public class DatabaseTaskRunner implements Serializable {

    private VirgilAPI myParent;

    private DaoMaster.DevOpenHelper favoritesDBHelper;
    private SQLiteDatabase favoritesDB;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private FavoriteMuseumDao favMuseumDao;
    private Context context;


    public List<FavoriteMuseum> favoriteList;

    public DatabaseTaskRunner (Context context, VirgilAPI parent) {
        this.context = context;
        this.myParent = parent;
        initDatabase();
    }

    private void initDatabase() {
        favoritesDBHelper = new DaoMaster.DevOpenHelper(this.context, "ORM.sqlite", null);
        favoritesDB = favoritesDBHelper.getWritableDatabase();

        //Get DaoMaster
        daoMaster = new DaoMaster(favoritesDB);

        //Create database and tables
        daoMaster.createAllTables(favoritesDB, true);

        daoSession = daoMaster.newSession();

        favMuseumDao = daoSession.getFavoriteMuseumDao();

        if (favMuseumDao.queryBuilder().where(
                FavoriteMuseumDao.Properties.Display.eq(true)).list() == null)
        {
            closeReopenDatabase();
        }

        favoriteList = favMuseumDao.queryBuilder().where(
                FavoriteMuseumDao.Properties.Display.eq(true)).list();

        if (favoriteList != null) {

            for (FavoriteMuseum guest : favoriteList)
            {
                if (guest == null)
                {
                    return;
                }

            }
        }
    }

    public boolean addFavorite(int id)
    {
        Log.d("DB", ("adding " + id));

        myParent.fetchMuseum(id);

        while (myParent.museumStatus() == myParent.PENDING_STATUS) {
            if (myParent.museumStatus() == myParent.ERROR_STATUS) {
                closeDatabase();
                return false;
            }
        }

        Museum museum = myParent.getMuseum();

        if (museum.getId() == 0) {
            Log.d("DB", "error fetching " + id);
            closeDatabase();
            return false;
        }

        Log.d("DB", "fetched " + myParent.getMuseum().getId());

        Random rand = new Random();

        String imageName = "museum_"+museum.getId()+".png";
        Bitmap museumImage = null;

        for (Content museumContent : museum.getContent()) {
            if (!museumContent.isMap()) {
                museumImage = (Bitmap) museumContent.getImage(this.context);
            }
        }

        FavoriteMuseum newFav = new FavoriteMuseum(rand.nextLong(), id, museum.getName(),
                museum.getAddress(), imageName, true, museumImage, this.context);

        favMuseumDao.insert(newFav);
        Log.d("DB", "added successfully");

        closeDatabase();
        return true;
    }


    public List<FavoriteMuseum> getFavorites() {
        return favoriteList;
    }

    public boolean deleteFavorite(int id) {

        for (FavoriteMuseum favMuseum : favoriteList) {
            if (favMuseum.getMuseumID() == id) {
                Log.d("DB", "Deleting museum " + id);

                ContextWrapper cw = new ContextWrapper(context);
                File directory = cw.getDir("favImageDir", Context.MODE_PRIVATE);
                File file =new File(directory, favMuseum.getPathToPicture());
                file.delete();

                favMuseumDao.delete(favMuseum);
                closeDatabase();
                return true;
            }
        }

        closeDatabase();
        return false;
    }

    private void closeDatabase()
    {
        Log.d("DB", "close");
        daoSession.clear();
        favoritesDB.close();
        favoritesDBHelper.close();
    }

    private void closeReopenDatabase()
    {
        Log.d("DB", "close reopen");
        closeDatabase();

        favoritesDBHelper = new DaoMaster.DevOpenHelper(this.context, "ORM.sqlite", null);
        favoritesDB = favoritesDBHelper.getWritableDatabase();

        //Get DaoMaster
        daoMaster = new DaoMaster(favoritesDB);

        //Create database and tables
        daoMaster.createAllTables(favoritesDB, true);

        //Create DaoSession
        daoSession = daoMaster.newSession();

        //Create customer addition/removal instances
        favMuseumDao = daoSession.getFavoriteMuseumDao();
    }
}