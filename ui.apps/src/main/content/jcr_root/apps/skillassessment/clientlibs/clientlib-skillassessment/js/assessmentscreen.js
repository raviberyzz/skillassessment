let currentQuestionIndex = 0;
const questions = document.querySelectorAll('.quizBody');
const totalQuestions = questions.length;
const nextButton = document.getElementById('nextButton');
const prevButton = document.getElementById('prevButton');
const selectedResponses = {};
const questionIds = {};
const totalTime =  document.getElementsByClassName('playground')[0].getAttribute('data-assessment-duration');
let timeRemaining = totalTime;
let countdown;

storeQuestionIds(questionIds);

function storeQuestionIds(questionIds) {
    questions.forEach((q, i) => {
        questionIds[i] = q.firstElementChild.getAttribute('id');
    });
}

function showQuestion(index) {
    questions.forEach((q, i) => {
        q.classList.toggle('quizBody-hidden', i !== index);
        if (i === index) {
            restoreSelectedOptions(index);
        }
    });
    document.getElementById('questionCount').textContent = index + 1;
    document.getElementById('totalQuestion').textContent = totalQuestions;
    updateButtonState();
}

function updateButtonState() {
    prevButton.style.display = currentQuestionIndex === 0 ? 'none' : 'inline-block';
    if (currentQuestionIndex === totalQuestions - 1) {
        nextButton.textContent = 'Submit';
        nextButton.removeEventListener('click', handleNext);
        nextButton.addEventListener('click', handleSubmit);
    } else {
        nextButton.textContent = 'Next';
        nextButton.removeEventListener('click', handleSubmit);
        nextButton.addEventListener('click', handleNext);
    }
}

function handleNext() {
    if (currentQuestionIndex < totalQuestions - 1) {
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    } else {
        showEndScreen();
    }
}

function handlePrevious() {
    if (currentQuestionIndex > 0) {
        currentQuestionIndex--;
        showQuestion(currentQuestionIndex);
    }
}

function handleSubmit() {
    if (confirm('Are you sure you want to submit your responses?')) {
        submitResponses();
    }
}

function showEndScreen() {
    document.querySelector('.playground').style.display = 'none';
    document.querySelector('.endScreen').style.display = 'flex';
    document.getElementById('resultUserName').textContent = document.getElementById('firstName').value;
}

function submitResponses() {

//clear the timer when submitting response within given timeframe.
    clearInterval(countdown); 

    const responses = Array.from({ length: totalQuestions }, (_, i) => ({
        id: questionIds[i],
        selectedOptions: selectedResponses[i] || []
    }));

    fetch(`${document.location.origin}`+'/bin/submit', { 
        method: 'POST',
        headers: {
            'Authorization': 'Basic YWRtaW46YWRtaW4=',
            'Content-Type': 'application/json',
            'Cookie': 'cq-authoring-mode=TOUCH'
        },
        body: JSON.stringify({
            user: {
                id: getCookieValue('email'),
                firstName: getCookieValue('firstName'),
                lastName: getCookieValue('lastName'),
                email: getCookieValue('email')
            },
            technology: document.getElementsByClassName('playground')[0].getAttribute('data-technology-value'),
            questions: responses
        })
    })
    .then(response => {
        if (response.ok) {
            return response.json().then(data => {             
                showDialog('Success', 'Responses submitted successfully!',data);

            });
        } else {
            return response.json().then(data => {
                showDialog('Failure', 'Submission failed: ' + (data.message || 'An error occurred'),data);

            });
        }
    })
    .catch(error => {
        showDialog('Failure', 'Submission failed: ' + error.message);
    });
}

function showDialog(title, message,data) {
    const dialog = document.createElement('div');
    dialog.className = 'dialog';
    dialog.innerHTML = `
      <div class="dialog-content">
        <h2>${title}</h2>
        <p>${message}</p>
        <button id="dialog-close">OK</button>
      </div>
    `;
    document.body.appendChild(dialog);

    document.getElementById('dialog-close').addEventListener('click', () => {
        document.body.removeChild(dialog);

    if (title === 'Success'){
        const showResult =  document.getElementsByClassName('playground')[0].getAttribute('data-show-result');
        if(showResult && showResult==="true"){
            showAssessmentReport(data);
        }else{
            removeCookiesAndRedirect();
			//window.location.href=window.location.origin+'/content/skillassessment/us/en/skill-assessment/login.html';
        }

    }else{
        removeCookiesAndRedirect();
		//window.location.href=window.location.origin+'/content/skillassessment/us/en/skill-assessment/login.html';
    }

    });
}

function deleteCookie(name) {
    document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/content/skillassessment/us/en;";
}

function removeCookiesAndRedirect() {
    deleteCookie("firstName");
    deleteCookie("lastName");
    deleteCookie("email");
    window.location.href=window.location.origin+'/content/skillassessment/us/en/skill-assessment/login.html';
}



function showAssessmentReport(response){
 const dialog = document.createElement('div');
     dialog.className='assessment-dialog';
    dialog.innerHTML= `
	<div class="assessment-dialog-content">
        <h2>${response.passed?'Congrats! You have passed the Assessment':'Sorry! You didnt passed the assessment.'}</h2>
        <p>${'you have scored '}<b>${response.percentageObtained+'%'}</b> </p>
         <button id="assessment-dialog-close">OK</button>
    </div>
    `;
         document.body.appendChild(dialog);

    document.getElementById('assessment-dialog-close').addEventListener('click', () => {
        document.body.removeChild(dialog);
      	// window.location.reload();
    removeCookiesAndRedirect();
   // window.location.href=window.location.origin+'/content/skillassessment/us/en/skill-assessment/login.html';
    });

}



function restoreSelectedOptions(questionIndex) {
    const selectedOptions = selectedResponses[questionIndex] || [];
    document.querySelectorAll(`#${questionIds[questionIndex]} .quizOptions li`).forEach(li => {
        li.classList.remove('selected');
        if (selectedOptions.includes(li.id)) {
            li.classList.add('selected');
        }
    });
}

document.getElementById('nextButton').addEventListener('click', handleNext);
document.getElementById('prevButton').addEventListener('click', handlePrevious);

document.querySelectorAll('.quizOptions li').forEach(li => {
    li.addEventListener('click', () => {
        const questionIndex = currentQuestionIndex;
        const selected = li.classList.toggle('selected');
        selectedResponses[questionIndex] = selectedResponses[questionIndex] || [];
        if (selected) {
            selectedResponses[questionIndex].push(li.id);

        } else {
            selectedResponses[questionIndex] = selectedResponses[questionIndex].filter(id => id !== li.id);
        }
    });
});



function startQuiz() {
    document.querySelector('.startScreen').style.display = 'none';
    document.querySelector('.playground').style.display = 'flex';
    showQuestion(currentQuestionIndex);
    // Start the timer when quiz starts
    startTimer(); 
}

function startTimer() {
    const timerElement = document.getElementById('timer');
     countdown = setInterval(() => {
        if (timeRemaining <= 0) {
            clearInterval(countdown);
            alert('Time expired! Your responses have been submitted.');
            submitResponses();
        } else {
            const minutes = Math.floor(timeRemaining / 60);
            const seconds = timeRemaining % 60;
            timerElement.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
            timeRemaining--;
        }
    }, 1000);
}

