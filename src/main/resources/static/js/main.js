/**
 * HobbyConnect - Frontend JavaScript
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Hobby Selection Counter
    const hobbyCheckboxes = document.querySelectorAll('.hobby-checkbox');
    const selectedCountBadge = document.getElementById('selected-count-badge');
    const submitHobbiesBtn = document.getElementById('save-hobbies-btn');

    function updateHobbyCount() {
        if (!hobbyCheckboxes.length || !selectedCountBadge) return;
        const checkedCount = document.querySelectorAll('.hobby-checkbox:checked').length;
        selectedCountBadge.textContent = checkedCount;

        if (submitHobbiesBtn) {
            if (checkedCount === 0) {
                submitHobbiesBtn.classList.add('disabled');
            } else {
                submitHobbiesBtn.classList.remove('disabled');
            }
        }
    }

    if (hobbyCheckboxes.length > 0) {
        hobbyCheckboxes.forEach(cb => {
            cb.addEventListener('change', updateHobbyCount);
        });
        updateHobbyCount();
    }

    // 2. Chat Window Auto Scroll to Bottom
    const chatMessagesContainer = document.getElementById('chat-messages-container');
    if (chatMessagesContainer) {
        chatMessagesContainer.scrollTop = chatMessagesContainer.scrollHeight;
    }

    // 3. Auto dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            try {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                bsAlert.close();
            } catch (e) {
                // Ignore if bootstrap is not loaded yet
            }
        }, 5000);
    });

    // 4. Quick filter on community search input
    const communityFilterInput = document.getElementById('community-search-input');
    const communityCards = document.querySelectorAll('.community-card-item');

    if (communityFilterInput && communityCards.length > 0) {
        communityFilterInput.addEventListener('input', function (e) {
            const query = e.target.value.toLowerCase().trim();
            communityCards.forEach(card => {
                const title = card.getAttribute('data-name')?.toLowerCase() || '';
                const desc = card.getAttribute('data-desc')?.toLowerCase() || '';
                const cat = card.getAttribute('data-category')?.toLowerCase() || '';

                if (title.includes(query) || desc.includes(query) || cat.includes(query)) {
                    card.style.display = '';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }
});
