"use client"

import {api} from "@/lib/api";
import {useEffect, useState} from "react";
import {fetchSessionData} from "@/util/sessionUtil";


export default function App() {
    const [email, setEmail] = useState("");

    function logout() {
        api.post("/user/logout")
            .then(response => console.log(response))
    }

    function myInfo() {
        api.get("/user/my-info")
            .then(response => {
                    console.log(response.data)
                    setEmail(response.data.email)
                }
            )
    }

    useEffect(() => {
        myInfo()
    })

    function kakaoLogin() {
        window.location.href = "http://localhost:8080/api/oauth2/authorization/kakao?redirect_uri=http://localhost:3000&mode=login"
    }

    fetchSessionData()
    return (
        <div>
            <div>
                {email}
            </div>
            <div>
                랜딩 페이지 입니다.
            </div>
            <div>
                <button>
                    <a href="/user/login">
                        로그인
                    </a>
                </button>
                <button
                    onClick={() => logout()}
                >
                    로그아웃
                </button>
            </div>
            <div>
                <button
                    onClick={kakaoLogin}>
                카카오 로그인
            </button>
            </div>
        </div>
    )
}