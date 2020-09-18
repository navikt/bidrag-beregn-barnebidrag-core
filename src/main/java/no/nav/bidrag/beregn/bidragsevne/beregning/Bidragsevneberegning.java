package no.nav.bidrag.beregn.bidragsevne.beregning;

import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;

public interface Bidragsevneberegning {
    ResultatBeregning beregn(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    Double beregnMinstefradrag(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    Double beregnSkattetrinnBelop(
        GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

    static Bidragsevneberegning getInstance(){
        return new BidragsevneberegningImpl();
    }


}
