function addParticipant() {
    const container = document.getElementById('participants-container');
    
    const participantDiv = document.createElement('div');
    participantDiv.className = 'participant';
    
    participantDiv.innerHTML = `
        <label class="dashboard__label">First Name:</label>
        <input type="text" class="dashboard__input participant__first-name" placeholder="Enter first name" required>
        
        <label class="dashboard__label">Last Name:</label>
        <input type="text" class="dashboard__input participant__last-name" placeholder="Enter last name" required>
        
        <label class="dashboard__label">Email:</label>
        <input type="email" class="dashboard__input participant__email" placeholder="Enter email address" required>
        
        <button onclick="removeParticipant(this)" class="participant__remove-button">Remove Participant</button>
    `;
    
    container.appendChild(participantDiv);
}

function removeParticipant(button) {
    const participantDiv = button.parentElement;
    participantDiv.remove();
}

function scheduleInterview() {
    const participants = [];
    document.querySelectorAll('.participant').forEach(participantDiv => {
        const firstName = participantDiv.querySelector('.participant__first-name').value;
        const lastName = participantDiv.querySelector('.participant__last-name').value;
        const email = participantDiv.querySelector('.participant__email').value;

        if (firstName && lastName && email) {
            participants.push({
                id: email,
                firstName: firstName,
                lastName: lastName,
                email: email
            });
        }
    });

    const technology = document.getElementById('technology').value;
    const leadsEmail = Array.from(document.getElementById('leads').selectedOptions).map(option => option.value);
    
    const payload = {
        participants: participants,
        recruiter: "recruiter@sunlife.com",
        leadsEmail: leadsEmail,
        technology: technology
    };
    
    console.log('Payload:', payload);

     fetch('/bin/schedule', {
        method: 'POST',
        headers: {
             'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
     })
      .then(response => {
        if (response.ok) {
            return response.json().then(data => {             
              //  showDialog('Success', 'Responses submitted successfully!',data);
                 alert ( "Responses submitted successfully!"); 
                 window.location.reload();


            });
        } else {
           return response.json().then(data => {
                alert(`'Failure, Submission failed: ' + ${data.message}`);
				window.location.reload();
            });
        }
    })
    .catch(error => {
        alert('Failure', 'Submission failed: ' + error.message);
			window.location.reload();
    });
}