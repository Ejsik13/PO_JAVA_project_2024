package agh.ics.oop.model;

import java.util.*;

public class Animal implements WorldElement {

    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private List<MoveDirection> moves;
    private int lifeLen;
    private int childrenCount;
    private final int genLen;
    private AnimalStatistics statistics; // Przechowuje statystyki

    public Animal(Vector2d position,int energy,int genLen) {
        this.direction = MapDirection.NORTH;
        this.position = position;
        this.energy = energy;
        this.moves = generateDefaultMoves(10); // Dodaj tę linię
        this.lifeLen = 0;
        this.childrenCount = 0;
        this.genLen = genLen;
        this.statistics = new AnimalStatistics(energy, moves);
    }

    public AnimalStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(AnimalStatistics statistics) {
        this.statistics = statistics;
    }

    public void incrementLifeLen() {
        this.lifeLen++;
        this.statistics.updateLifeLen(this.lifeLen); // Aktualizuj statystykę w AnimalStatistics
    }

    public void incrementChildrenCount() {
        childrenCount++;
    }

    public MoveDirection getMostCommonGenom() {
        // Mapa do zliczania wystąpień każdego kierunku
        Map<MoveDirection, Integer> frequencyMap = new HashMap<>();

        for (MoveDirection move : moves) {
            frequencyMap.put(move, frequencyMap.getOrDefault(move, 0) + 1);
        }

        // Znajdź kierunek z największą liczbą wystąpień
        return frequencyMap.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null); // Zwróć null, jeśli lista jest pusta
    }

    public int getLifeLen() {
        return lifeLen;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setMoves(List<MoveDirection> moves) {
        this.moves = moves;
    }

    public int getEnergy() {
        return energy;
    }

    public void incrementEnergy(int increment) {
        this.energy += increment;
    }

    public List<MoveDirection> getMoves() {
        return moves;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    private List<MoveDirection> generateDefaultMoves(int length) {
        Random random = new Random();
        List<MoveDirection> moves = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            moves.add(MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
        }
        return moves;
    }

    public Animal() {
        this(new Vector2d(2,2),10,10);
    }

    @Override
    public String toString() {
        return this.direction.toString();
    }
    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public MapDirection getDirection() {
        return direction;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void rotate(int count) {
        for (int index=0;index<count;index++) {
            direction = direction.next();
        }
    }

    //za pomocą tych funkcji co były np na lewo czy na prawo
    public void move(int direction, MoveValidator validator, int mapWidth, int mapHeight) {
        // Obrót zwierzaka
        for (int i = 0; i < direction; i++) {
            this.direction = this.direction.next();
        }

        // Oblicz nową pozycję
        Vector2d newPosition = this.position.add(this.direction.toUnitVector());

        // Odbijanie w pionie (góra-dół)
        if (newPosition.getY() >= mapHeight || newPosition.getY() < 0) {
            this.direction = this.direction.opposite(); // Obrót o 180°
            newPosition = this.position.add(this.direction.toUnitVector()); // Oblicz nową pozycję po odbiciu
        }

        if (newPosition.getX() >= mapWidth) {
            newPosition = new Vector2d(0, newPosition.getY()); // Przejście na lewą krawędź
        } else if (newPosition.getX() < 0) {
            newPosition = new Vector2d(mapWidth - 1, newPosition.getY()); // Przejście na prawą krawędź
        }

        // Sprawdź, czy nowa pozycja jest dostępna
        if (validator.canMoveTo(newPosition)) {
            this.position = newPosition;
        }

        // Zmniejszenie energii
        this.energy--;

        // Debugowanie
        System.out.println("Animal moved to " + this.position + ", remaining energy: " + this.energy);
    }

}