# KIDE Product Release Webapp

Production-style Python web application implementing thesis workflow concepts:
- Multi-language intake (currently JSON representation for activity language)
- Synthesis services (commands/events/alarms/datapoints)
- Composition service (assembled control node / MNC model)
- Browser UI + REST API

## Run
```bash
python app.py
```

## Endpoints
- `GET /api/health`
- `POST /api/transform`

## Architecture
- `kide_product/parsers`: language parsers
- `kide_product/services/synthesis.py`: synthesis stage
- `kide_product/services/composition.py`: composition stage
- `kide_product/services/transformer.py`: orchestration
