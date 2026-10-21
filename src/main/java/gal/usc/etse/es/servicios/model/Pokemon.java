package gal.usc.etse.es.servicios.model;

public class Pokemon {
    private int id;
    private String name;
    private List<PokemonType> types;
    private Stats stats;
    private List<String> moves;

    public Pokemon() {}

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonType> types) {
        this.types = types;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }
}
