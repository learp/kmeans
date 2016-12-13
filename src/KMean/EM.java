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
        Matrix weight = new Matrix(clusterCount, 1, 1d/clusterCount);

        Matrix _mathExpectation = new Matrix(clusterCount, dimension);
        Matrix _cov = new Matrix(dimension, dimension);
        Matrix _weight = new Matrix(clusterCount, 1, 1d/clusterCount);

        Matrix x = new Matrix(points.getRowDimension(), clusterCount);

        Random random = new Random(37);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < clusterCount; j++) {
                mathExpectation.set(j, i, random.nextDouble());
            }
            cov.set(i, i, 1);
        }

        double lgCur = 0, lgPrev;
        long iter = 0;

        do {
            lgPrev = lgCur;
            _mathExpectation = new Matrix(clusterCount, dimension);
            _cov = new Matrix(dimension, dimension);
            _weight = new Matrix(clusterCount, 1, 1d/clusterCount);

            for (int i = 0; i < points.getRowDimension(); i++) {
                double spi = 0;

                for (int j = 0; j < clusterCount; j++) {
                    double qij = points.getMatrix(i, i, 0, dimension - 1).minus(mathExpectation.getMatrix(j, j, 0, dimension - 1)).
                    times(cov.inverse()).
                    times(
                            points.getMatrix(i, i, 0, dimension - 1)
                                    .minus(mathExpectation.getMatrix(j, j, 0, dimension - 1)).transpose()
                    ).get(0, 0);

                    double pij = weight.get(j, 0) / (Math.pow((2 * Math.PI), dimension / 2d) * Math.sqrt(cov.det())) * Math.exp(-1d/2  * qij);
                    x.set(i, j, pij);
                    spi += pij;
                }

                x.setMatrix(
                        i, i,
                        0, clusterCount - 1,
                        x.getMatrix(i, i, 0, clusterCount - 1).times(1/spi));
                lgCur += Math.log(spi);

                _mathExpectation.plusEquals(
                        points.getMatrix(i, i, 0, dimension - 1).transpose().times(
                            x.getMatrix(i, i, 0, clusterCount - 1)
                        ).transpose()
                );

                _weight.plusEquals(x.getMatrix(i, i, 0, clusterCount - 1).transpose());
            }

            for (int j = 0; j < clusterCount; j++) {
                mathExpectation.setMatrix(0, _mathExpectation.getRowDimension() - 1, 0, _mathExpectation.getColumnDimension() - 1, _mathExpectation.times(_weight.get(j, 0)));

                for (int i = 0; i < points.getRowDimension(); i++) {
                    _cov.plusEquals(
                            points.getMatrix(i, i, 0, dimension - 1).minus(mathExpectation.getMatrix(j, j, 0, dimension - 1))
                            .times(x.get(i,j)).transpose()
                            .times((points.getMatrix(i, i, 0, dimension - 1)
                            .minus(mathExpectation.getMatrix(j, j, 0, dimension - 1)))));
                }
            }

            cov.setMatrix(
                    0, _cov.getColumnDimension() - 1,
                    0, _cov.getColumnDimension() - 1,
                    _cov.times(1d/points.getRowDimension()));

            weight.setMatrix(
                    0, _weight.getColumnDimension() - 1,
                    0, _weight.getColumnDimension() - 1,
                    _weight.times(1d/points.getRowDimension()));

        } while(Math.abs(lgCur - lgPrev) >= epsilon && iter++ < 300);

        x.print(points.getRowDimension(), clusterCount);
    }

    int clusterCount;
    Matrix points;
    int dimension;
    double epsilon;
}
