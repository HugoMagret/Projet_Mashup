package fr.univangers.movies.model;

public class  MovieModelFactory{

    private final static String MODEL_CLASS_NAME = "fr.univangers.movies.model.MovieModelImpl";
    private static MovieModel service = null;

    private MovieModelFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static MovieModel getInstance() {
        try {
            Class serviceClass = Class.forName(MODEL_CLASS_NAME);
            return (MovieModel) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static MovieModel getModel() {

        if (service == null) {
            service = getInstance();
        }
        return service;

    }
}