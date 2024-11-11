import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import SubBanner from "../SubBanner";
import "./Login.css";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const [loginStatus] = useState("");

  const handleLogin = () => {
    axios.post('api/api/auth/login', {
        email: email,
        password: password,
    })
    .then((response) => {
        
        const userData = response.data;
        console.log(userData.data.user);
        sessionStorage.setItem('userData', JSON.stringify(userData.data.user));

        const userType = JSON.parse(sessionStorage.getItem("userData")).userType

        if(userType == "2"){
          navigate('/');
        } else{
          navigate("/")
        }
        
    })
    .catch((error) => {
        console.error("Login error:", error);
        alert("로그인 실패. 이메일 또는 비밀번호를 확인해주세요.");
    });
};


  return (
    <div>
      <SubBanner page_name={"storage"} title_en={"Login"} title_kr={"로그인"} />
      <div>
        <div className="loginbox">
          <input
            type="email"
            name="userEmail"
            placeholder="이메일 주소"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="loginbutton">
          <button
            className="button_text outline"
            onClick={() => navigate("/signup")}
          >
            회원가입
          </button>
          <button
            className="button_text primary"
            onClick={
              handleLogin
            }
          >
            로그인
          </button>
        </div>
        {loginStatus && <div className="loginStatus">{loginStatus}</div>}
      </div>
    </div>
  );
}

export default LoginPage;
