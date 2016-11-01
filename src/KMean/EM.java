package KMean;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EM implements Runnable {

    public EM(Collection<Point> points, int clusterCount) {
        this(points, clusterCount, 1e-6);
    }

    public EM(Collection<Point> points, int clusterCount, double epsilon) {

        this.epsilon = epsilon;
    }

    @Override
    public void run() {
        double mathExpectation[][] = new double[dimension][clusters.size()];
        double cov[][] = new double[dimension][dimension];
        double weight[] = new double[clusters.size()];
        double x[] = new double[clusters.size()];

        Random random = new Random(37);
        for (int i = 0; i < dimension; i++) {
            cov[i][i] = 1;

            for (int j = 0; j < clusters.size(); j++) {
                mathExpectation[i][j] = random.nextDouble();
                weight[j] = 1/clusters.size();
            }
        }

        double lgCur = 0, lgPrev = 0;

        do {
            for (int i = 0; i < points.size(); i++) {
                double spi = 0;

                for (int j = 0; j < clusters.size(); j++) {

                }

            }

        } while(Math.abs(lgCur - lgPrev) >= epsilon);
    }

    List<Cluster> clusters;
    Set<Point> points;
    int dimension;
    double epsilon;
}
