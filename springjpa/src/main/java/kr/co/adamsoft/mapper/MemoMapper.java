package kr.co.adamsoft.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import kr.co.adamsoft.dto.MemoDTO;

@Repository
public interface MemoMapper {
    @Select("select * from tbl_memo")
    public List<MemoDTO> listMemo();
}
