package com.lottoweb.dto;

import java.util.List;
import java.util.Map;

/**
 * 로또 히스토리 조회 결과를 담는 DTO
 * 
 * ⭐ 왜 복합적인 구조의 DTO를 만들까요?
 * 1. 한 번의 API 호출로 모든 정보 제공: 페이징, 필터링, 통계 정보를 한 번에
 * 2. 클라이언트 효율성: 여러 번의 API 호출 없이 한 번에 필요한 데이터 제공
 * 3. 일관성: 모든 히스토리 관련 정보를 하나의 응답으로 통합
 * 
 * 응답 예시:
 * {
 *   "items": [...],              // 로또 번호 리스트
 *   "pagination": {              // 페이징 정보
 *     "currentPage": 1,
 *     "totalPages": 100,
 *     "totalCount": 2000,
 *     "size": 20
 *   },
 *   "filters": {                 // 적용된 필터
 *     "start": 1000,
 *     "end": 2000,
 *     "number": 7
 *   },
 *   "statistics": {              // 통계 정보
 *     "frequencies": {...},      // 각 번호의 출현 빈도
 *     "mostFrequent": {          // 가장 많이 나온 번호
 *       "number": 7,
 *       "count": 100
 *     },
 *     "leastFrequent": {        // 가장 적게 나온 번호
 *       "number": 1,
 *       "count": 50
 *     }
 *   }
 * }
 */
public class LottoHistoryResponse {
    
    private List<LottoNumberResponse> items;      // 로또 번호 리스트
    private PaginationInfo pagination;            // 페이징 정보
    private FilterInfo filters;                   // 필터 정보
    private StatisticsInfo statistics;             // 통계 정보
    
    // 기본 생성자
    public LottoHistoryResponse() {}
    
    // 전체 생성자
    public LottoHistoryResponse(List<LottoNumberResponse> items, PaginationInfo pagination, 
                               FilterInfo filters, StatisticsInfo statistics) {
        this.items = items;
        this.pagination = pagination;
        this.filters = filters;
        this.statistics = statistics;
    }
    
    // Getter와 Setter
    public List<LottoNumberResponse> getItems() {
        return items;
    }
    
    public void setItems(List<LottoNumberResponse> items) {
        this.items = items;
    }
    
    public PaginationInfo getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }
    
    public FilterInfo getFilters() {
        return filters;
    }
    
    public void setFilters(FilterInfo filters) {
        this.filters = filters;
    }
    
    public StatisticsInfo getStatistics() {
        return statistics;
    }
    
    public void setStatistics(StatisticsInfo statistics) {
        this.statistics = statistics;
    }
    
    /**
     * 페이징 정보를 담는 내부 클래스
     */
    public static class PaginationInfo {
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private int totalCount;     // 전체 데이터 수
        private int size;           // 페이지당 데이터 수
        
        public PaginationInfo() {}
        
        public PaginationInfo(int currentPage, int totalPages, int totalCount, int size) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
            this.size = size;
        }
        
        // Getter와 Setter
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
    
    /**
     * 필터 정보를 담는 내부 클래스
     */
    public static class FilterInfo {
        private Integer start;      // 시작 회차
        private Integer end;        // 종료 회차
        private Integer number;     // 포함할 번호
        
        public FilterInfo() {}
        
        public FilterInfo(Integer start, Integer end, Integer number) {
            this.start = start;
            this.end = end;
            this.number = number;
        }
        
        // Getter와 Setter
        public Integer getStart() { return start; }
        public void setStart(Integer start) { this.start = start; }
        public Integer getEnd() { return end; }
        public void setEnd(Integer end) { this.end = end; }
        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }
    }
    
    /**
     * 통계 정보를 담는 내부 클래스
     */
    public static class StatisticsInfo {
        private Map<Integer, Integer> frequencies;  // 각 번호의 출현 빈도
        private FrequencyInfo mostFrequent;          // 가장 많이 나온 번호
        private FrequencyInfo leastFrequent;         // 가장 적게 나온 번호
        
        public StatisticsInfo() {}
        
        public StatisticsInfo(Map<Integer, Integer> frequencies, FrequencyInfo mostFrequent, 
                             FrequencyInfo leastFrequent) {
            this.frequencies = frequencies;
            this.mostFrequent = mostFrequent;
            this.leastFrequent = leastFrequent;
        }
        
        // Getter와 Setter
        public Map<Integer, Integer> getFrequencies() { return frequencies; }
        public void setFrequencies(Map<Integer, Integer> frequencies) { this.frequencies = frequencies; }
        public FrequencyInfo getMostFrequent() { return mostFrequent; }
        public void setMostFrequent(FrequencyInfo mostFrequent) { this.mostFrequent = mostFrequent; }
        public FrequencyInfo getLeastFrequent() { return leastFrequent; }
        public void setLeastFrequent(FrequencyInfo leastFrequent) { this.leastFrequent = leastFrequent; }
    }
    
    /**
     * 빈도 정보를 담는 내부 클래스
     */
    public static class FrequencyInfo {
        private int number;    // 번호
        private int count;     // 출현 횟수
        
        public FrequencyInfo() {}
        
        public FrequencyInfo(int number, int count) {
            this.number = number;
            this.count = count;
        }
        
        // Getter와 Setter
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
