import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '1m', target: 100 }, // Ramp up to 100 users
        { duration: '3m', target: 1000 }, // Ramp up to 1000 users
        { duration: '1m', target: 0 },   // Ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    },
};

export default function () {
    let res = http.get('http://localhost:8080/api/health');
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1);
}
