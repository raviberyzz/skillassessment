if(getCookieValue('firstName')){
document.getElementsByClassName('user-info')[0].lastElementChild.innerHTML= `${getCookieValue('firstName')}`;
}



function getCookieValue(name) {
    return document.cookie
        .split('; ')
        .find(row => row.startsWith(name + '='))
        ?.split('=')[1] || null;
}

