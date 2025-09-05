package backend.futurefinder.repository.jpa.word;

import backend.futurefinder.jpaentity.economic.EconomicWordJpaEntity;
import backend.futurefinder.jparepository.word.WordJpaRepository;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.word.PopularWord;
import backend.futurefinder.model.word.ScrapWord;
import backend.futurefinder.model.word.WordId;
import backend.futurefinder.repository.word.WordRepository;
import backend.futurefinder.util.SortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepository {

    private final WordJpaRepository wordJpaRepository;

    @Override
    @Transactional
    public boolean append(UserId userId, String word, String meaning) {
        final String uid = userId.getId();

        return wordJpaRepository.findByUserIdAndWordName(uid, word)
                .map(entity -> false) // 이미 존재 → 저장 안 함, false
                .orElseGet(() -> {
                    EconomicWordJpaEntity created = EconomicWordJpaEntity.create(word, meaning, uid);
                    wordJpaRepository.save(created);
                    return true; // 새로 생성됨
                });
    }

    @Override
    public List<ScrapWord> reads(UserId userId, int page){
        Sort sort = SortType.LATEST.toSort().and(Sort.by(Sort.Direction.DESC, "wordId"));
        Pageable pageable = PageRequest.of(page, 5, sort);

        List<EconomicWordJpaEntity> rows =
                wordJpaRepository.findAllByUserId(userId.getId(), pageable);

        return rows.stream()
                .map(e -> ScrapWord.of(WordId.of(e.getWordId()), e.getWordName(), e.getMeaning()))
                .toList();
    }

    @Override
    public List<PopularWord> readPopularNames() {
        Pageable top10 = PageRequest.of(0, 10); // LIMIT 10
        List<String> names = wordJpaRepository.findPopularWordNames(top10);
        return names.stream()
                .map(PopularWord::of)
                .toList();
    }





}
