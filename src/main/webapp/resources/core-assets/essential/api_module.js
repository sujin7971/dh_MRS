/**
 * 
 */
const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;
	  
//object에서 key에 할당된 value가 undefined이거나 null인 경우 그 key를 제거
const filterUndefinedAndNull = (obj) => {
	return Object.fromEntries(
		Object.entries(obj).filter(([key, value]) => value !== undefined && value !== null)
	);
};
//url에 parameter 할당
const appendQueryParams = (url, queryParams) => {
	const queryString = new URLSearchParams(filterUndefinedAndNull(queryParams)).toString();
	return queryString ? `${url}?${queryString}` : url;
};
const fetchData = (url, method = "GET", data = null, queryParams = {}, headers = {}) => {
	  const requestUrl = appendQueryParams(url, queryParams);
	  

	  const defaultHeaders = {
	    "Content-Type": "application/json",
	    "X-Requested-With": "XMLHttpRequest",
	  };

	  if (csrfToken && csrfHeader && !["GET", "HEAD", "OPTIONS", "TRACE"].includes(method.toUpperCase())) {
	    defaultHeaders[csrfHeader] = csrfToken;
	  }

	  const options = {
	    method,
	    headers: {
	      ...defaultHeaders,
	      ...headers,
	    },
	  };
	  if(data){
		  if (options.headers["Content-Type"] === "multipart/form-data") {
			  delete options.headers["Content-Type"];
			  options.body = data instanceof FormData ? data : new FormData();
		  } else if (options.headers["Content-Type"].includes("application/x-www-form-urlencoded")) {
			  options.body = data instanceof FormData ? data : new URLSearchParams(data).toString();
		  } else {
			  options.body = data && JSON.stringify(data);
		  }
	  }
	  console.log('Request', 'requestUrl:',requestUrl, 'data:', data, 'Options:', options);
	  return fetch(requestUrl, options)
	    .then((response) => {
	      if (!response.ok) {
	        //throw new Error(`HTTP error ${response.status}`);
	    	  throw response;
	      }
	    	if (response.headers.get("Content-Length") === "0") {
    	      return null;
    	    }
	    	const contentType = response.headers.get("Content-Type");
	        if (contentType && contentType.includes("application/json")) {
	        	try {
	      	      return response.json();
	      	    } catch (error) {
	      	      return null;
	      	    }
	        } else {
	        	return response.text();
	        }
	    })
	    .catch((error) => {
	      console.error("Fetch error:", error);
	      throw error;
	    });
	};

const Get = (url, queryParams = {}) =>
  fetchData(url, "GET", null, filterUndefinedAndNull(queryParams), {});

const Post = (url, { data = null, queryParams = {}, headers = {} } = {}) =>
  fetchData(url, "POST", data, filterUndefinedAndNull(queryParams), headers);

const Put = (url, { data = null, queryParams = {}, headers = {} } = {}) =>
  fetchData(url, "PUT", data, filterUndefinedAndNull(queryParams), headers);

const Delete = (url, { data = null, queryParams = {}, headers = {} } = {}) =>
  fetchData(url, "DELETE", data, filterUndefinedAndNull(queryParams), headers);

const Api = {
  Get,
  Post,
  Put,
  Delete,
};

export default Api;