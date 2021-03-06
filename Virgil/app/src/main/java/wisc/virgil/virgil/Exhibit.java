package wisc.virgil.virgil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TylerPhelps on 3/18/16.
 */
public class Exhibit implements Serializable {
    private int id, galleryId, museumId;
    private String name, description;
    private ArrayList<Content> content;

    public Exhibit(int id, int galleryId, int museumId, String name, String description) {
        this.id = id;
        this.galleryId = galleryId;
        this.museumId = museumId;
        this.name = name;
        this.content = new ArrayList<>();
    }

    public int getId() { return this.id; }

    public int getGalleryId() { return this.galleryId; }

    public int getMuseumId() { return this.museumId; }

    public String getDescription() { return this.description; }

    public String getName() {
        return this.name;
    }

    public boolean addContent(Content content) {
        return this.content.add(content);
    }

    public ArrayList<Content> getContent() {
        return this.content;
    }
}
