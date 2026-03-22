import axios    from "axios";
import {ElMessage} from "element-plus";


const defaultError = ()  => ElMessage.error('发生错误，请联系管理员')
const defaultFailure = (message) => ElMessage.warning(message)


function post(url, data, success, failure = defaultFailure, error = defaultError){
    const params = new URLSearchParams();
    for (let key in data) {
        params.append(key, data[key]);
    }
    axios.post(url, params, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        withCredentials: true
    }).then(({data}) =>{
    if (data.success)
        success(data.message,data.status);
    else
        failure(data.message,data.status);
    }).catch(error => error(error));
}
function get(url,success, failure = defaultFailure, error = defaultError){
    axios.get(url, {
        withCredentials: true
    }).then(({data}) =>{
        if (data.success)
            success(data.message,data.status);
        else
            failure(data.message,data.status);
    }).catch(error => failure(error));
}

export {get,post}