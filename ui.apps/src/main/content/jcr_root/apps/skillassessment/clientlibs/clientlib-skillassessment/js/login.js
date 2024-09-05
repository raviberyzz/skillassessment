function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Create a FormData object to hold the form data
    const formData = new FormData();
    formData.append('j_username', username.trim());
    formData.append('j_password', password.trim());

    fetch('/content/skillassessment/us/en/skill-assessment/login.html/j_security_check', { 
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {

				fetch('/bin/authenticate', { 
                method: 'GET',
                credentials: 'include'
                }).then(resp =>{
                        if(resp.ok){
                    window.location.href = window.location.origin+"/content/skillassessment/us/en/redirect.html"
                }else{
						alert('Incorrect UserName or Password');
                	}

                        });



        } else {
            throw new Error('Login failed');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Login failed. Please try again.');
    });
}

const formsubmit=document.getElementById("formsubmit");
if(formsubmit){
    formsubmit.addEventListener("click", function(event){
    event.preventDefault();
    login();
});
}