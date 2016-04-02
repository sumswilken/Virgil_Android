package wisc.virgil.virgil;

import java.util.ArrayList;
import android.util.Log;
import java.util.List;

/**
 * Created by TylerPhelps on 3/19/16.
 */
public class VirgilAPI {

    private final String GET_MUSEUM = "getMuseum";
    private final String GET_ALL_MUSEUMS = "getAllMuseums";
    public final int PENDING_STATUS = 0;
    public final int ERROR_STATUS = -1;
    public final int FINISHED_STATUS = 1;

    public Museum museum;
    public ArrayList<Museum> museumList;
    public boolean listFinished;

    public List<FavoriteMuseum> favoriteList;
    public boolean dbTaskFinished, dbSuccess;

    public VirgilAPI() {
        this.museum = null;
        this.museumList = null;
        this.listFinished = false;

        this.favoriteList = null;
        this.dbTaskFinished = false;
        this.dbSuccess = false;
    }

    public void fetchMuseum(int id) {
        BackendTaskRunner runner = new BackendTaskRunner(this);
        runner.execute(GET_MUSEUM, Integer.toString(id));
    }

    public void fetchMuseum(Museum museum) {
        fetchMuseum(museum.getId());
    }

    public void fetchAllMuseums() {
        BackendTaskRunner runner = new BackendTaskRunner(this);
        runner.execute(GET_ALL_MUSEUMS);
    }

    public ArrayList<Museum> getMuseumList() {
        Log.d("API", "getting " + this.museumList.size() + " museums.");
        return this.museumList;
    }

    public Museum getMuseum() {
        return this.museum;
    }

    public int museumStatus() {
        if (this.museum != null) {
            if (this.museum.getName().isEmpty()) {
                return this.ERROR_STATUS;
            }
            else return this.FINISHED_STATUS;
        }
        else return this.PENDING_STATUS;
    }

    public int museumListStatus() {
        if (this.listFinished) {
            return this.FINISHED_STATUS;
        }
        else if (this.museumList != null) {
            return this.PENDING_STATUS;
        }
        else {
            return this.ERROR_STATUS;
        }
    }

    public void refreshFavorites() {
        DatabaseTaskRunner runner = new DatabaseTaskRunner(this);
        runner.execute("refresh");
    }

    public List<FavoriteMuseum> getFavorites() {
        return this.favoriteList;
    }

    public boolean getDBSuccess() {
        return this.dbSuccess;
    }

    public void addFavorite(int id) {
        DatabaseTaskRunner runner = new DatabaseTaskRunner(this);
        runner.execute("add", Integer.toString(id));
    }

    public void deleteFavorite(int id) {
        DatabaseTaskRunner runner = new DatabaseTaskRunner(this);
        runner.execute("delete", Integer.toString(id));
    }

    public void addFavorite(Museum favMuseum) {
        addFavorite(museum.getId());
    }

    public void deleteFavorite(Museum favMuseum) {
        deleteFavorite(museum.getId());
    }
}
