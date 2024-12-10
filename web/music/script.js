// script.js
const progress = document.getElementById("progress");
const swiperWrapper = document.querySelector(".swiper-wrapper");
const song = document.getElementById("song");
const controlIcon = document.getElementById("controlIcon");
const playPauseButton = document.querySelector(".play-pause-btn");
const forwardButton = document.querySelector(".controls button.forward");
const backwardButton = document.querySelector(".controls button.backward");
const songName = document.querySelector(".music-player h1");
const artistName = document.querySelector(".music-player p");

let songs = [];
let currentSongIndex = 0;

// Fetch radio stations and populate Swiper
document.addEventListener("DOMContentLoaded", async () => {
  try {
    const stationsResponse = await fetch("http://localhost:7070/stations");
    const stations = await stationsResponse.json();

    swiperWrapper.innerHTML = ""; // Clear existing slides

    stations.forEach((station) => {
      const slide = document.createElement("div");
      slide.className = "swiper-slide";
      slide.innerHTML = `<img src="${station.logo}" alt="${station.name}" />`;
      swiperWrapper.appendChild(slide);

      // Add song data for the station
      songs.push({
        title: station.rds.title || "Unknown Title",
        name: station.rds.artist || "Unknown Artist",
        source: station.stream,
      });
    });

    // Reverse the order of songs after all are added
    initializeSwiper();
    updateSongInfo();

    setInterval(updateSongTitlesAndArtists, 15000);

  } catch (error) {
    console.error("Error fetching stations:", error);
  }
});

async function updateSongTitlesAndArtists() {
  try {
    const stationsResponse = await fetch("http://localhost:7070/stations");
    const stations = await stationsResponse.json();

    stations.forEach((station, index) => {
      if (songs[index]) {
        songs[index].title = station.rds?.title || "Unknown Title";
        songs[index].name = station.rds?.artist || "Unknown Artist";
      }
    });

    // Update current song info if playing
    updateSongInfo(false);

  } catch (error) {
    console.error("Error updating song titles and artists:", error);
  }
}

function initializeSwiper() {
  new Swiper(".swiper", {
    effect: "coverflow",
    centeredSlides: true,
    slidesPerView: "auto",
    allowTouchMove: false,
    spaceBetween: 40,
    coverflowEffect: {
      rotate: 25,
      stretch: 0,
      depth: 50,
      modifier: 1,
      slideShadows: false,
    },
    navigation: {
      nextEl: ".forward",
      prevEl: ".backward",
    },
  });
}

function updateSongInfo(forceUpdate = true) {
  if (songs.length === 0) return;

  songName.textContent = songs[currentSongIndex].title;
  artistName.textContent = songs[currentSongIndex].name;

  if (forceUpdate || song.src !== songs[currentSongIndex].source) {
    song.src = songs[currentSongIndex].source;
  }
}

song.addEventListener("timeupdate", function () {
  if (!song.paused) {
    progress.value = song.volume * 100.0;
  }
});

song.addEventListener("loadedmetadata", function () {
  progress.max = 100;
  progress.value = 50;
});

function pauseSong() {
  song.pause();
  controlIcon.classList.remove("fa-pause");
  controlIcon.classList.add("fa-play");
}

function playSong() {
  song.play();
  controlIcon.classList.add("fa-pause");
  controlIcon.classList.remove("fa-play");
}

function playPause() {
  if (song.paused) {
    playSong();
  } else {
    pauseSong();
  }
}

playPauseButton.addEventListener("click", playPause);

progress.addEventListener("input", function () {
  song.volume = progress.value / 100.0;
});

forwardButton.addEventListener("click", function () {
  currentSongIndex = (currentSongIndex + 1) % songs.length;
  updateSongInfo();
  playSong();
});

backwardButton.addEventListener("click", function () {
  currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length;
  updateSongInfo();
  playSong();
});
