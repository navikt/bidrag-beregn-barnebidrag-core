package no.nav.bidrag.beregn.bidragsevne.beregning;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;

public interface Bidragsevneberegning {
    ResultatBeregning beregn(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    BigDecimal beregnMinstefradrag(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    BigDecimal beregnSkattetrinnBelop(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    static Bidragsevneberegning getInstance(){
        return new BidragsevneberegningImpl();
    }


}
