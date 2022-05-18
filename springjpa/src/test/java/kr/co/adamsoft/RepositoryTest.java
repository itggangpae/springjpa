package kr.co.adamsoft;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.adamsoft.entity.Memo;
import kr.co.adamsoft.entity.QMemo;
import kr.co.adamsoft.repository.MemoRepository;

@SpringBootTest
public class RepositoryTest {
	@Autowired
	MemoRepository memoRepository;

	//주입 확인
	//@Test
	public void testDependency(){
		System.out.println("주입 여부:" + memoRepository.getClass().getName());
	}

	//삽입 확인
	//@Test
	public void testInsert(){
		IntStream.rangeClosed(1,100).forEach(i -> {
			Memo memo = Memo.builder().memoText("Sample..."+i).build();
			memoRepository.save(memo);
		});
	}

	@Test
	public void testSelect(){
		Long mno = 1L;
		Optional<Memo> result = memoRepository.findById(mno);
		System.out.println("==================================");
		if(result.isPresent()){
			Memo memo = result.get();
			System.out.println(memo);
		}
	}

	//데이터 수정
	//@Test
	public void testUpdate() {
		Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();
		System.out.println(memoRepository.save(memo));
	}

	//데이터 삭제
	//@Test
	public void testDelete() {
		Long mno = 100L;
		memoRepository.deleteById(mno);
	}

	//페이징
	//@Test
	public void testPageDefault() {
		//1페이지 10개
		Pageable pageable = PageRequest.of(0,10);
		Page<Memo> result = memoRepository.findAll(pageable);
		System.out.println(result);
		System.out.println ("--------------------------------------------------------------- ");
		System.out.println("Total Pages: "+result.getTotalPages()); //전체 페이지 개수
		System.out.println("Total Count: "+result.getTotalElements()); //전체 데이터 개수
		System.out.println("Page Number: "+result. getNumber ()); //현재 페이지 번호 0부터 시작
		System.out.println("Page Size: "+result .getSize()); //페이지당 데이터 개수
		System.out.println("Has next page?:"+ result.hasNext()); //다음 페이지존재 여부
		System.out.println("First page?: "+result.isFirst()); //시작 페이지 (0) 여부
		System.out.println("---------------------------------------------------------");

		//데이터 순회
		for (Memo memo : result.getContent()) {
			System.out.println(memo);
		}
	}

	//@Test
	public void testSort() {
		Sort sort1 = Sort.by("mno").descending();
		Pageable pageable = PageRequest.of(0, 10, sort1);
		Page<Memo> result = memoRepository.findAll(pageable);
		result.get().forEach(memo -> {
			System.out.println(memo);
		});
	}

	//@Test
	public void testSortConcate() {
		Sort sort1 = Sort.by("mno").descending();
		Sort sort2 = Sort.by("memoText").ascending();
		Sort sortAll = sort1.and(sort2); //and를 이용한 연결
		Pageable pageable = PageRequest.of(0, 10, sortAll); //결합된 정렬 조건 사용

		Page<Memo> result = memoRepository.findAll(pageable);
		result.get().forEach(memo -> {
			System.out.println(memo);
		});
	}

	//@Test
	public void testQueryMethods(){
		List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L,80L);
		for (Memo memo : list) {
			System.out.println(memo);
		}
	}

	//@Test
	public void testQueryMethodsPaging(){
		Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());

		Page<Memo> result = memoRepository.findByMnoBetween(10L,50L, pageable);

		result.get().forEach(memo -> System.out.println(memo));
	}

	//작업을 완료하기 위해서 설정
	@Commit
	//설정하지 않으면 에러
	@Transactional
	//@Test
	public void testDeleteQueryMethods() {
		memoRepository.deleteMemoByMnoLessThan(10L);
	}

	//@Test
	public void testUpdateQuery(){
		System.out.println(memoRepository.updateMemoText(11L, "@Query를 이용한 수정"));
		System.out.println(memoRepository.updateMemoText(Memo.builder().mno(12L).memoText("@Query를 이용한 수정").build()));
	}

	//@Test
	public void testSelectQuery(){
		Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
		Page<Memo> page = memoRepository.getListWithQuery(50L, pageable );
		for(Memo memo : page){
			System.out.println(memo);
		}
	}

	//@Test
	public void testSelectQueryObjectReturn(){
		Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
		Page<Object []> page = memoRepository.getListWithQueryObject(50L, pageable );
		for(Object [] ar : page){
			System.out.println(Arrays.toString(ar));
		}
	}

	@Test
	public void testSelectNativeQuery(){	
		List<Object []> list = memoRepository.getNativeResult();
		for(Object [] ar : list){
			System.out.println(Arrays.toString(ar));
		}
	}

	@PersistenceContext
	EntityManager em;

	@Test
	@DisplayName("Querydsl 조회 테스트1")
	public  void queryDslTest(){
	        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
	        QMemo qMemo = QMemo.memo;
	        JPAQuery<Memo> query  = queryFactory.selectFrom(qMemo)
	                .where(qMemo.mno.eq(12L));
	        List<Memo> memoList = query.fetch();
	        for(Memo memo : memoList){
	            System.out.println(memo.toString());
	        }
	}
	
	//@Test
	@DisplayName("상품 Querydsl 조회 테스트 2")
	public void queryDslTest2(){
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		QMemo memo = QMemo.memo;
		String memoText = "Sample";
		int mno = 50;
		booleanBuilder.and(memo.memoText.like("%" + memoText + "%"));
		booleanBuilder.and(memo.mno.gt(mno));
		Pageable pageable = PageRequest.of(0, 5);
		Page<Memo> momoPagingResult = memoRepository.findAll(booleanBuilder, pageable);
		System.out.println("total elements : " + momoPagingResult.getTotalElements());
		List<Memo> resultMemoList = momoPagingResult.getContent();
		for(Memo resultMemo: resultMemoList){
			System.out.println(resultMemo.toString());
		}
	}



}

