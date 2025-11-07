package com.lottoweb.dao;

import com.lottoweb.model.LottoNumber;
import com.lottoweb.model.LottoWeightCalculator;
import com.lottoweb.repository.LottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LottoDAO {

	private final LottoRepository lottoRepository;  // JPA Repository

	@Autowired
	public LottoDAO(LottoRepository lottoRepository) {
		this.lottoRepository = lottoRepository;
	}

	// 최신 로또 번호 가져오기
	// ⭐ JPA로 전환: Repository를 사용합니다
	public LottoNumber getLottoNumber() {
		return lottoRepository.findFirstByOrderByPostgameDesc()
				.orElse(null);  // Optional을 null로 변환
	}

	// 로또 번호들 가져오기 (가중치 계산용)
	// ⭐ JPA로 전환: Repository를 사용하여 모든 로또 번호를 가져옵니다
	public List<Integer> getAllLottoNumbers() {
		List<LottoNumber> allLottos = lottoRepository.findAll();
		List<Integer> allNumbers = new ArrayList<>();
		
		for (LottoNumber lotto : allLottos) {
			allNumbers.add(lotto.getNum1());
			allNumbers.add(lotto.getNum2());
			allNumbers.add(lotto.getNum3());
			allNumbers.add(lotto.getNum4());
			allNumbers.add(lotto.getNum5());
			allNumbers.add(lotto.getNum6());
		}
		
		return allNumbers;
	}

	// 로또 번호 추첨 (가중치 기반)
	public Map<String, Object> drawLottoNumbers() {
		List<Integer> allNumbers = getAllLottoNumbers();
		Map<Integer, Double> weights = LottoWeightCalculator.calculateWeights(allNumbers);
		return LottoWeightCalculator.drawLotto(weights, 6);
	}

	// 특정 번호를 반드시 포함하여 추첨 (가중치 기반, 중복 없음)
	public Map<String, Object> drawLottoNumbersIncluding(int includeNumber) {
		if (includeNumber < 1 || includeNumber > 45) return drawLottoNumbers();
		List<Integer> allNumbers = getAllLottoNumbers();
		Map<Integer, Double> weights = LottoWeightCalculator.calculateWeights(allNumbers);

		// 포함 번호는 미리 선택하고, 가중치 목록에서 제거한 뒤 나머지 5개를 뽑는다
		Map<Integer, Double> filtered = new LinkedHashMap<>(weights);
		filtered.remove(includeNumber);

		Map<String, Object> partial = LottoWeightCalculator.drawLotto(filtered, 5);
		@SuppressWarnings("unchecked")
		List<Integer> selected = (List<Integer>) partial.get("drawnNumbers");
		if (!selected.contains(includeNumber)) selected.add(includeNumber);
		Collections.sort(selected);

		// 보너스는 중복 없이 선택
		Random r = new Random();
		int bonus;
		do { bonus = r.nextInt(45) + 1; } while (selected.contains(bonus));

		Map<String, Object> result = new HashMap<>();
		result.put("drawnNumbers", selected);
		result.put("bonusNumber", bonus);
		return result;
	}

	// 전체 히스토리 건수
	// ⭐ JPA로 전환: count() 메서드는 JpaRepository에서 자동 제공됩니다
	public int countLottoHistory() {
		return (int) lottoRepository.count();
	}

	// 페이징 히스토리 조회 (postgame DESC)
	// ⭐ JPA로 전환: Pageable을 사용한 페이징
	public List<LottoNumber> getLottoHistory(int offset, int limit) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		return lottoRepository.findAllByOrderByPostgameDesc(pageable).getContent();
	}

	// 필터링된 전체 건수 (회차범위/번호 포함)
	// ⭐ JPA로 전환: Repository의 @Query 메서드 사용
	public int countLottoHistory(Integer startPostgame, Integer endPostgame, Integer includeNumber) {
		return (int) lottoRepository.countWithFilters(startPostgame, endPostgame, includeNumber);
	}

	// 필터링 + 페이징 조회 + 정렬
	// ⭐ JPA로 전환: Repository의 @Query 메서드 사용 (정렬은 현재는 postgame DESC로 고정)
	public List<LottoNumber> getLottoHistory(Integer startPostgame, Integer endPostgame, Integer includeNumber, int offset, int limit, String sortCol, String sortDir) {
		// TODO: 정렬 옵션(sortCol, sortDir)을 동적으로 지원하려면 Repository에 추가 메서드 필요
		Pageable pageable = PageRequest.of(offset / limit, limit);
		return lottoRepository.findWithFilters(startPostgame, endPostgame, includeNumber, pageable);
	}

	// 필터된 전체 행에 대한 번호 빈도(1~45), 보너스 포함
	// ⭐ JPA로 전환: Repository의 findWithFilters를 사용하여 필터링된 데이터를 가져옵니다
	public Map<Integer, Integer> countFrequencies(Integer startPostgame, Integer endPostgame, Integer includeNumber) {
		Map<Integer, Integer> freq = new LinkedHashMap<>();
		for (int i = 1; i <= 45; i++) freq.put(i, 0);
		
		// 필터링된 로또 번호들을 가져옴 (페이징 없이 전체 데이터)
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<LottoNumber> filteredLottos = lottoRepository.findWithFilters(
			startPostgame, endPostgame, includeNumber, pageable);
		
		// 각 로또 번호의 6개 번호와 보너스 번호를 카운트
		for (LottoNumber lotto : filteredLottos) {
			increment(freq, lotto.getNum1());
			increment(freq, lotto.getNum2());
			increment(freq, lotto.getNum3());
			increment(freq, lotto.getNum4());
			increment(freq, lotto.getNum5());
			increment(freq, lotto.getNum6());
			increment(freq, lotto.getBonusnum());
		}
		
		return freq;
	}

	private void increment(Map<Integer, Integer> map, int key) {
		if (key >= 1 && key <= 45) map.put(key, map.get(key) + 1);
	}
}

