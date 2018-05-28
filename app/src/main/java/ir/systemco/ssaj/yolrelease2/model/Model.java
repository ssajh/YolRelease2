package ir.systemco.ssaj.yolrelease2.model;

/**
 * Created by RVCO on 4/9/2018.
 */

public class Model {

    private int imageId;
    private String MovieTitle;
    private String MovieDesc;

    public Model(int imageId, String movieTitle, String movieDesc) {
        this.imageId = imageId;
        MovieTitle = movieTitle;
        MovieDesc = movieDesc;
    }



    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getMovieTitle() {
        return MovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        MovieTitle = movieTitle;
    }

    public String getMovieDesc() {
        return MovieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        MovieDesc = movieDesc;
    }
}
