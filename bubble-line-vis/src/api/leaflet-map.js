import request from '@/utils/request'

export function getHouseList(page) {
    return request({
        url: '/api/vis/house',
        method: 'get',
        params: {...page}
    })
}

export function getPopulationList(page) {
    return request({
        url: '/api/vis/population',
        method: 'get',
        params: {...page}
    })
}

export function getScatters(page) {
    return request({
        url: '/api/vis/scatter',
        method: 'get',
        params: {...page}
    })
}
