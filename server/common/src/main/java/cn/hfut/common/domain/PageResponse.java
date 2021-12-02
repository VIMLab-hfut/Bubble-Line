package cn.hfut.common.domain;

import java.io.Serializable;
import java.util.List;

public class PageResponse implements Serializable {

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> data;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /**
     * 表格数据对象
     */
    public PageResponse()
    {
    }

    public PageResponse(long total, List<?> data, int code, String msg) {
        this.total = total;
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public PageResponse(List<?> list, int total)
    {
        this.data = list;
        this.total = total;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "total=" + total +
                ", data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<?> getData()
    {
        return data;
    }

    public void setData(List<?> data)
    {
        this.data = data;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}
