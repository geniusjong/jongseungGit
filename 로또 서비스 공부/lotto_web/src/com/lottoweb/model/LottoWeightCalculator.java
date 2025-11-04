package com.lottoweb.model;

import java.util.*;
import java.util.stream.Collectors;

public class LottoWeightCalculator {
	/**
	 * 과거 당첨 번호 집합으로부터 1~45 숫자에 대한 가중치를 계산한다.
	 *
	 * 입력 목록이 비어있으면 균일 가중치(모두 1.0)를 반환한다.
	 * 가중치는 (빈도 기반 50%) + (정규분포 기반 50%)의 가중 합으로 계산되며,
	 * 마지막에 합이 1이 되도록 정규화한다. 합이 0/NaN이면 균일 가중치로 대체한다.
	 */
    public static Map<Integer, Double> calculateWeights(List<Integer> allNumbers) {
		// 데이터가 없으면 균일 가중치 반환
		if (allNumbers == null || allNumbers.isEmpty()) {
			Map<Integer, Double> uniform = new HashMap<>();
			for (int i = 1; i <= 45; i++) {
				uniform.put(i, 1.0);
			}
			return uniform;
		}

		// 1) 빈도수 계산: 과거 데이터에서 각 숫자가 등장한 비율
		//    존재하지 않은 숫자는 0으로 취급한다.
		Map<Integer, Long> frequency = allNumbers.stream()
				.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));

		long totalCount = allNumbers.size();

		Map<Integer, Double> freqWeights = new HashMap<>();
		for (int i = 1; i <= 45; i++) {
			double freqWeight = frequency.getOrDefault(i, 0L) / (double) totalCount;
			freqWeights.put(i, freqWeight);
		}

		// 2) 정규분포 기반 가중치: 평균에 가까울수록 큰 가중치
		//    표준편차가 0이거나 비정상인 경우 균일 가중치로 대체한다.
		double mean = allNumbers.stream().mapToInt(Integer::intValue).average().orElse(0);
		double variance = allNumbers.stream().mapToDouble(num -> Math.pow(num - mean, 2)).average().orElse(0);
		double stdDev = Math.sqrt(variance);

		Map<Integer, Double> normalWeights = new HashMap<>();
		if (stdDev <= 1e-9 || Double.isNaN(stdDev)) {
			for (int i = 1; i <= 45; i++) {
				normalWeights.put(i, 1.0);
			}
		} else {
			for (int i = 1; i <= 45; i++) {
				double w = Math.exp(-Math.pow(i - mean, 2) / (2 * Math.pow(stdDev, 2)));
				normalWeights.put(i, w);
			}
		}

		// 3) 결합 가중치: 빈도(0.5) + 정규(0.5)
		Map<Integer, Double> combinedWeights = new HashMap<>();
		for (int i = 1; i <= 45; i++) {
			combinedWeights.put(i, 0.5 * freqWeights.getOrDefault(i, 0.0) + 0.5 * normalWeights.getOrDefault(i, 0.0));
		}

		// 4) 정규화: 합이 1이 되도록 스케일링 (합이 0/NaN이면 균일 가중치로 대체)
		double sum = combinedWeights.values().stream().mapToDouble(Double::doubleValue).sum();
		if (sum <= 1e-12 || Double.isNaN(sum)) {
			combinedWeights.clear();
			for (int i = 1; i <= 45; i++) {
				combinedWeights.put(i, 1.0);
			}
		} else {
			for (Map.Entry<Integer, Double> e : combinedWeights.entrySet()) {
				e.setValue(e.getValue() / sum);
			}
		}

		return combinedWeights;
	}

	/**
	 * 가중치 맵을 이용해 k개의 번호를 비복원 추출한다.
	 *
	 * 각 단계에서 누적 가중치로 룰렛 선택을 수행하고,
	 * 선택된 항목의 가중치를 정확히(totalWeight에서) 차감한다.
	 * 보너스 번호는 1~45 범위에서 중복 없이 균일 무작위로 선택한다.
	 */
    public static Map<String, Object> drawLotto(Map<Integer, Double> weights, int k) {
		// 번호 목록과 해당 가중치 목록 준비 (인덱스 동기화 유지)
		List<Integer> numbers = new ArrayList<>(weights.keySet());
		List<Double> weightsList = new ArrayList<>(weights.values());
 
		// 선택된 번호를 누적 저장
		List<Integer> selectedNumbers = new ArrayList<>();
		double totalWeight = weightsList.stream().mapToDouble(Double::doubleValue).sum();
 
		for (int i = 0; i < k; i++) {
			// 0 ~ totalWeight 사이에서 무작위 선택값 생성
			double rand = Math.random() * totalWeight;
			double cumulativeWeight = 0.0;
 
			for (int j = 0; j < numbers.size(); j++) {
				cumulativeWeight += weightsList.get(j);
				if (rand <= cumulativeWeight) {
					// 선택된 인덱스의 가중치를 정확히 차감
					double removedWeight = weightsList.get(j);
					selectedNumbers.add(numbers.get(j));
					numbers.remove(j);
					weightsList.remove(j);
					totalWeight -= removedWeight;
					break;
				}
			}
		}
 		
		// 최종 번호 정렬
		Collections.sort(selectedNumbers);
 
		// 보너스 번호 선택: 이미 뽑힌 번호는 제외하여 중복 방지
		Random rand = new Random();
		int bonusNum;
		do {
			bonusNum = rand.nextInt(45) + 1; 
		} while (selectedNumbers.contains(bonusNum)); 
      
		// (가독성) 한 번 더 정렬 보장
		Collections.sort(selectedNumbers);
         
		// 결과 묶음
		Map<String, Object> result = new HashMap<>();
		result.put("drawnNumbers", selectedNumbers);
		result.put("bonusNumber", bonusNum);
         
		return result;
    }
    
}
