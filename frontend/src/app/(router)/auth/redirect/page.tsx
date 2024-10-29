"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { signIn } from "next-auth/react";

export default function App() {
    const router = useRouter();

    useEffect(() => {
        // 현재 URL에서 쿼리 매개변수 가져오기
        const queryString = window.location.search; // 쿼리 문자열
        const urlParams = new URLSearchParams(queryString); // URLSearchParams 객체 생성

        const accessToken = urlParams.get("accessToken");
        const refreshToken = urlParams.get("refreshToken");

        if (accessToken && refreshToken) {
            signIn("kakao", {
                redirect: false,
                accessToken,
                refreshToken,
            })
                .then(() => router.push("/"));
        }
    }, [router]);

    return <div>Redirecting...</div>;
}
