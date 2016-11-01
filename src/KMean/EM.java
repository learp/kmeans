package KMean;

import Jama.Matrix;

import java.util.*;

public class EM implements Runnable {

    public EM(Collection<Point> points, int clusterCount) {
        this(points, clusterCount, 1e-6);
    }

    public EM(Collection<Point> points, int clusterCount, double epsilon) {
        prepareAndCheck(points);

        double tmp[][] = new double[points.size()][dimension];
        Iterator<Point> point = points.iterator();

        for (int i = 0; i < points.size(); i++) {
            tmp[i] = Arrays.copyOf(
                    point.next().getVector().stream().mapToDouble(Double::doubleValue).toArray(), dimension
            );
        }

        this.points = new Matrix(tmp, points.size(), dimension);
        this.clusterCount = clusterCount;
        this.epsilon = epsilon;
    }

    private void prepareAndCheck(Collection<Point> points) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("list of poins is empty");
        }

        Iterator<Point> it = points.iterator();
        dimension = it.next().vector.size();
        while (it.hasNext()) {
            if (dimension != it.next().vector.size()) {
                throw new IllegalArgumentException("different size");
            }
        }
    }

    @Override
    public void run() {
        Matrix mathExpectation = new Matrix(clusterCount, dimension);
        Matrix cov = new Matrix(dimension, dimension);
        Matrix weight = new Matrix(clusterCount, 1, 1/clusterCount);
        Matrix x = new Matrix(clusterCount, 1);

        Random random = new Random(37);
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < dimension; j++) {
                mathExpectation.set(i, j, random.nextDouble());
                cov.set(j, j, 1);
            }
        }

        double lgCur = 0, lgPrev = 0;

        do {
            lgPrev = lgCur;

            for (int i = 0; i < points.getRowDimension(); i++) {
                double spi = 0;

                for (int j = 0; j < clusterCount; j++) {
                    double qij = points.getMatrix(i, i, 0, dimension - 1).minus(mathExpectation.getMatrix(j, j, 0, dimension - 1)).transpose().
                    times(cov.inverse()).
                    times(points.getMatrix(i, i, 0, dimension - 1).minus(mathExpectation.getMatrix(j, j, 0, dimension - 1))).get(0, 0);
                    System.out.println(qij);
                    double pij = weight.get(j, 1) / (Math.pow((2 * Math.PI), dimension / 2) * Math.sqrt(cov.det())) * Math.exp(-1/2  * qij);
                    spi += pij;
                }

            }

        } while(Math.abs(lgCur - lgPrev) >= epsilon);
    }

    int clusterCount;
    Matrix points;
    int dimension;
    double epsilon;
}
