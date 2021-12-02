package cn.hfut.gpv.service;

import cn.hfut.gpv.domain.GisPipe;
import cn.hfut.gpv.domain.HeatLine;
import cn.hfut.gpv.domain.dto.GisPipeDTO;
import cn.hfut.gpv.domain.vo.PipelineVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PipeService {

    void deleteById(Integer id);

    void insertSelective(GisPipe record);

    GisPipe selectById(Integer id);

    void updateByIdSelective(GisPipe record);

    List<GisPipe> listPipeSelective(GisPipeDTO condition);

    List<PipelineVO> listPipelineVO();

    PipelineVO getPipelineVOById(Integer id);
}
