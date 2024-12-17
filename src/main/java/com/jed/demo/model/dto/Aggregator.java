package com.jed.demo.model.dto;

import java.util.*;

public class Aggregator {
    public Map<String, RequestStat> aggregate(
            Map<String, List<RequestInfo>> requestInfos, long durationInMillis) {
        Map<String, RequestStat> requestStats = new HashMap<>();
        for (Map.Entry<String, List<RequestInfo>> entry : requestInfos.entrySet()) {
            String apiName = entry.getKey();
            List<RequestInfo> requestInfosPerApi = entry.getValue();
            RequestStat requestStat = doAggregate(requestInfosPerApi, durationInMillis);
            requestStats.put(apiName, requestStat);
        }
        return requestStats;
    }

    private RequestStat doAggregate(List<RequestInfo> requestInfos, long durationInMillis) {
        List<Double> respTimes = new ArrayList<>();
        for (RequestInfo requestInfo : requestInfos) {
            double respTime = requestInfo.getResponseTime();
            respTimes.add(respTime);
        }
        RequestStat requestStat = new RequestStat();
        requestStat.setMaxResponseTime(max(respTimes));
        requestStat.setMinResponseTime(min(respTimes));
        requestStat.setAvgResponseTime(avg(respTimes));
        requestStat.setP999ResponseTime(percentile999(respTimes));
        requestStat.setP99ResponseTime(percentile99(respTimes));
        requestStat.setCount(respTimes.size());
        requestStat.setTps((long) tps(respTimes.size(), durationInMillis / 1000));
        return requestStat;
    }

    private double max(List<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be null or empty");
        }
        return Collections.max(dataset);
    }

    private double min(List<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be null or empty");
        }
        return Collections.min(dataset);
    }

    private double avg(List<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("Dataset cannot be null or empty");
        }
        return dataset.stream().mapToDouble(Double::doubleValue).average().orElseThrow(() -> new IllegalArgumentException("Cannot calculate average"));
    }

    private double tps(int count, double duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        return count / duration;
    }

    private double percentile999(List<Double> dataset) {
        return calculatePercentile(dataset, 99.9);
    }

    private double percentile99(List<Double> dataset) {
        return calculatePercentile(dataset, 99.0);
    }

    private double calculatePercentile(List<Double> dataset, double percentile) {
        if (dataset == null || dataset.isEmpty() || percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Invalid arguments for calculating percentile");
        }
        Collections.sort(dataset);
        int index = (int) Math.ceil(percentile / 100.0 * dataset.size());
        index = Math.max(1, index); // Ensure the index is at least 1
        return dataset.get(index - 1);
    }

}
