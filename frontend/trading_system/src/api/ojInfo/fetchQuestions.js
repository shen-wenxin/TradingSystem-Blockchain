import HttpService from "../../config/axios";

/**
 * 获取所有题目信息
 * */

export function selectAllQuestions(){
    return HttpService({
        url:'/question/selectAllQuestions',
        method:'get'
    })
}

/**
 * 根据题号获取对应题目信息
 * @param questionId 题号
 * */
export function selectQuestionById(questionId){
    return HttpService({
        url:'/question/selectAllQuestions',
        method:'get',
        params:{
            id:questionId
        }
    })
}
