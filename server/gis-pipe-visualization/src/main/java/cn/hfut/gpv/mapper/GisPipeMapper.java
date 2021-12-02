package cn.hfut.gpv.mapper;

import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.domain.vo.PipelineVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GisPipeMapper {
    int deleteById(Integer id);

    int insertSelective(GisPipe record);

    GisPipe selectById(Integer id);

    int updateByIdSelective(GisPipe record);

    List<GisPipe> listPipeSelective(GisPipeDTO condition);

    List<PipelineVO> listPipelineVO(@Param("pipeId")Integer pipeId, @Param("groupNumber") Integer groupNumber);
}