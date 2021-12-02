import request from '@/utils/request'

export function getList(page) {
    return request({
        url: '/api/gis/pipe/list',
        method: 'get',
        params: {...page}
    })
}

export function getMarkerList(page) {
    return request({
        url: '/api/gis/marker/list',
        method: 'get',
        params: {...page}
    })
}

export function addHeatLine(data) {
    return request({
        url: '/api/vis/heat_line/add',
        method: 'post',
        data:{...data},
    })
}

export function updatePipe(data) {
    return request({
        url: '/api/gis/pipe/update',
        method: 'post',
        data:{...data}
    })
}

export function deletePipe(id) {
    return request({
        url: '/api/gis/pipe/delete',
        method: 'post',
        params:{id}
    })
}

export function addMarker(data) {
    return request({
        url: '/api/gis/marker/add',
        method: 'post',
        data:{...data},
    })
}

export function updateMarker(data) {
    return request({
        url: '/api/gis/marker/update',
        method: 'post',
        data:{...data}
    })
}

export function deleteMarker(id) {
    return request({
        url: '/api/gis/marker/delete',
        method: 'post',
        params:{id}
    })
}
