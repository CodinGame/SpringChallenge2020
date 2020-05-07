package com.codingame.view.event;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewerEvent {

    /**
     * Join multiple object into a space separated string
     */
    static private String join(Object... args) {
        return Stream.of(args).map(String::valueOf).collect(Collectors.joining(" "));
    }

    private double t;
    private String id;
    public Map<String, Object> params;
    private static DecimalFormat decimalFormat;
    static {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("0.######");
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setDecimalFormatSymbols(otherSymbols);
    }

    static String formatFrameTime(double t) {
        return decimalFormat.format(t);
    }

    public ViewerEvent(String id, double t) {
        this.t = t;
        this.id = id;
        this.params = new HashMap<>();
    }

    public ViewerEvent(String id) {
        this(id, 0);
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        String serializedParams = params.entrySet().stream()
            .sorted(Entry.comparingByKey())
            .map(Entry::getValue)
            .map(String::valueOf)
            .collect(Collectors.joining(" "));
        return join(formatFrameTime(t), id, serializedParams);
    }

}