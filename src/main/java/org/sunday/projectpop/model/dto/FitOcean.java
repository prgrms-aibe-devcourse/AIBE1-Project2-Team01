package org.sunday.projectpop.model.dto;

public record FitOcean(double o, double c, double e, double a, double n) {
    public OCEAN minBoundary(int range) {
        return new OCEAN(
                (int) Math.floor(o-range),
                (int) Math.floor(c-range),
                (int) Math.floor(e-range),
                (int) Math.floor(a-range),
                (int) Math.floor(n-range));
    }
    public OCEAN maxBoundary(int range) {
        return new OCEAN(
                (int) Math.floor(o+range),
                (int) Math.floor(c+range),
                (int) Math.floor(e+range),
                (int) Math.floor(a+range),
                (int) Math.floor(n+range));
    }
}
