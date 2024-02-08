package Filters;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private List<Point> points;
    private Point centroid;

    public Cluster(Point centroid) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }
}