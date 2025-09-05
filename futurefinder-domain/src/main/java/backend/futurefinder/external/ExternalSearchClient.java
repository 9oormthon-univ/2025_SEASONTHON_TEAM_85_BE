package backend.futurefinder.external;

import java.util.Optional;

public interface ExternalSearchClient {

    /**
     * @param term 검색어(예: "인플레이션")
     * @return 뜻/요약 텍스트 (없으면 empty)
     */
    Optional<String> lookupMeaning(String term);


}
