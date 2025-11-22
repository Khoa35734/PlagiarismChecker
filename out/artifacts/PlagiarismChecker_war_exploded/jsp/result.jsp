<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Plagiarism Check Results</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            color: white;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
        }

        .results-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }

        .result-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }

        .result-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 50px rgba(0,0,0,0.15);
        }

        .file-name {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
            text-align: center;
            word-break: break-word;
        }

        .chart-container {
            position: relative;
            width: 200px;
            height: 200px;
            margin: 0 auto 30px;
        }

        .circular-chart {
            transform: rotate(-90deg);
        }

        .circle-bg {
            fill: none;
            stroke: #f0f0f0;
            stroke-width: 3.8;
        }

        .circle {
            fill: none;
            stroke-width: 3.8;
            stroke-linecap: round;
            animation: progress 1s ease-out forwards;
        }

        @keyframes progress {
            0% {
                stroke-dasharray: 0 100;
            }
        }

        .percentage {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 2.5rem;
            font-weight: bold;
            color: #333;
        }

        .legend {
            margin-top: 20px;
        }

        .legend-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .legend-item:last-child {
            border-bottom: none;
        }

        .legend-label {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .legend-color {
            width: 16px;
            height: 16px;
            border-radius: 50%;
        }

        .color-unique {
            background: #9BCF53;
        }

        .color-exact {
            background: #FF6B6B;
        }

        .color-partial {
            background: #4ECDC4;
        }

        .legend-value {
            font-weight: 600;
            color: #333;
        }

        .status-badge {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 600;
            text-align: center;
            margin: 20px auto;
            display: block;
            width: fit-content;
        }

        .status-no-issues {
            background: #d4edda;
            color: #155724;
        }

        .status-partial {
            background: #fff3cd;
            color: #856404;
        }

        .status-suspected {
            background: #f8d7da;
            color: #721c24;
        }

        .congratulations {
            text-align: center;
            margin-top: 20px;
        }

        .congratulations img {
            max-width: 200px;
            margin-bottom: 15px;
        }

        .congratulations h3 {
            color: #9BCF53;
            font-size: 1.5rem;
            margin-bottom: 10px;
        }

        .congratulations p {
            color: #666;
        }

        .matched-segments {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #f0f0f0;
        }

        .matched-segments h4 {
            color: #333;
            margin-bottom: 15px;
            font-size: 1.1rem;
        }

        .segment-item {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 10px;
            border-left: 4px solid #FF6B6B;
        }

        .segment-text {
            color: #555;
            font-size: 0.9rem;
            margin-bottom: 8px;
            line-height: 1.5;
        }

        .segment-source {
            color: #888;
            font-size: 0.85rem;
            font-style: italic;
        }

        .error {
            background: white;
            padding: 20px;
            border-radius: 10px;
            color: #721c24;
            text-align: center;
        }

        .back-button {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 30px;
            background: white;
            color: #667eea;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .back-button:hover {
            background: #667eea;
            color: white;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>📄 Plagiarism Check Results</h1>
        <p>Kết quả kiểm tra đạo văn</p>
    </div>

    <c:if test="${not empty error}">
        <div class="error">
            <h3>⚠️ Error</h3>
            <p>${error}</p>
            <a href="${pageContext.request.contextPath}/upload" class="back-button">← Back to Upload</a>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty results}">
            <div class="result-card" style="text-align: center;">
                <h3>No results found</h3>
                <p style="color: #666; margin: 20px 0;">Upload files to start checking for plagiarism.</p>
                <a href="${pageContext.request.contextPath}/upload" class="back-button">← Upload Files</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="results-grid">
                <c:forEach var="row" items="${results}">
                    <c:set var="uniquePercent" value="${100 - (row.simWinnowing * 100)}" />
                    <c:set var="exactPercent" value="${row.simWinnowing * 100}" />
                    <c:set var="partialPercent" value="${0}" />

                    <%-- Determine chart color based on similarity --%>
                    <c:choose>
                        <c:when test="${exactPercent > 50}">
                            <c:set var="chartColor" value="#FF6B6B" />
                            <c:set var="chartPercent" value="${exactPercent}" />
                            <c:set var="chartLabel" value="Đạo văn" />
                        </c:when>
                        <c:when test="${exactPercent > 20}">
                            <c:set var="chartColor" value="#FFA500" />
                            <c:set var="chartPercent" value="${exactPercent}" />
                            <c:set var="chartLabel" value="Khả nghi" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="chartColor" value="#9BCF53" />
                            <c:set var="chartPercent" value="${uniquePercent}" />
                            <c:set var="chartLabel" value="Độc nhất" />
                        </c:otherwise>
                    </c:choose>

                    <div class="result-card">
                        <div class="file-name">📄 ${row.filename}</div>

                        <div class="chart-container">
                            <svg viewBox="0 0 36 36" class="circular-chart">
                                <path class="circle-bg"
                                      d="M18 2.0845
                                         a 15.9155 15.9155 0 0 1 0 31.831
                                         a 15.9155 15.9155 0 0 1 0 -31.831"
                                />
                                <path class="circle"
                                      stroke="${chartColor}"
                                      stroke-dasharray="${chartPercent}, 100"
                                      d="M18 2.0845
                                         a 15.9155 15.9155 0 0 1 0 31.831
                                         a 15.9155 15.9155 0 0 1 0 -31.831"
                                />
                            </svg>
                            <div class="percentage">
                                <fmt:formatNumber value="${chartPercent}" maxFractionDigits="0" />%
                            </div>
                        </div>

                        <div class="legend">
                            <div class="legend-item">
                                <div class="legend-label">
                                    <div class="legend-color color-unique"></div>
                                    <span>Độc nhất</span>
                                </div>
                                <div class="legend-value">
                                    <fmt:formatNumber value="${uniquePercent}" maxFractionDigits="1" />%
                                </div>
                            </div>
                            <div class="legend-item">
                                <div class="legend-label">
                                    <div class="legend-color color-exact"></div>
                                    <span>Exact</span>
                                </div>
                                <div class="legend-value">
                                    <fmt:formatNumber value="${exactPercent}" maxFractionDigits="1" />%
                                </div>
                            </div>
                            <div class="legend-item">
                                <div class="legend-label">
                                    <div class="legend-color color-partial"></div>
                                    <span>Partial</span>
                                </div>
                                <div class="legend-value">
                                    <fmt:formatNumber value="${partialPercent}" maxFractionDigits="1" />%
                                </div>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${row.status == 'No Issues'}">
                                <div class="status-badge status-no-issues">✓ ${row.status}</div>
                                <div class="congratulations">
                                    <h3>Congratulations</h3>
                                    <p>Plagiarism not found!</p>
                                </div>
                            </c:when>
                            <c:when test="${row.status == 'Partial Match'}">
                                <div class="status-badge status-partial">⚠ ${row.status}</div>
                            </c:when>
                            <c:otherwise>
                                <div class="status-badge status-suspected">⚠ ${row.status}</div>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${not empty row.matchedSegmentsJson and row.matchedSegmentsJson != '[]'}">
                            <div class="matched-segments">
                                <h4>🔍 Xem các nguồn đạo văn</h4>
                                <c:set var="segments" value="${row.matchedSegmentsJson}" />
                                <div style="font-size: 0.85rem; color: #666;">
                                    <c:if test="${not empty row.sourceDocument}">
                                        <p><strong>Source:</strong> ${row.sourceDocument}</p>
                                    </c:if>
                                    <p><strong>Similarity Score:</strong>
                                        <fmt:formatNumber value="${row.simWinnowing * 100}" maxFractionDigits="2" />%
                                    </p>
                                </div>
                            </div>
                        </c:if>

                        <div style="text-align: center; margin-top: 20px;">
                            <small style="color: #999;">
                                Uploaded: <fmt:formatDate value="${row.uploadTime}" pattern="yyyy-MM-dd HH:mm" />
                            </small>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div style="text-align: center; margin-top: 30px;">
                <a href="${pageContext.request.contextPath}/upload" class="back-button">← Check Another Document</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
