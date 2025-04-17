import React from "react";
import Link from "next/link";
import Image from "next/image";
import img1 from "@/assets/hugohub_logo.jpeg";
import "@/components/home-component/Home.css";

function Home() {
  return (
    <div className="homeContainer">
      <div className="logoContainer">
        <Image
          src={img1}
          alt="Company Logo"
          className="homeLogo"
          width={250}
          height={250}
        />
      </div>
      <div className="buttonContainer">
        <Link href="/login" className="homeButton loginButton">
          Login
        </Link>
        <Link href="/signup" className="homeButton signupButton">
          Signup
        </Link>
      </div>
    </div>
  );
}

export default Home;
